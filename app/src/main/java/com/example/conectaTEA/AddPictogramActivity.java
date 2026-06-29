package com.example.conectaTEA;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPictogramActivity extends BaseActivity {

    private EditText etPictogramName, etPictogramLink, etPictogramCategory, etPictogramBorderColor;
    private Button btnSavePictogram;
    private String tableId, passedImageUrl, localImageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pictogram);

        setupBackButton();

        etPictogramName = findViewById(R.id.etPictogramName);
        etPictogramLink = findViewById(R.id.etPictogramLink);
        etPictogramCategory = findViewById(R.id.etPictogramCategory);
        etPictogramBorderColor = findViewById(R.id.etPictogramBorderColor);
        btnSavePictogram = findViewById(R.id.btnSavePictogram);

        tableId = getIntent().getStringExtra("TABLE_ID");
        passedImageUrl = getIntent().getStringExtra("IMAGE_URL");
        localImageUriString = getIntent().getStringExtra("LOCAL_IMAGE_URI");

        if (localImageUriString != null && !localImageUriString.trim().isEmpty()) {
            etPictogramLink.setText("Imagem selecionada da galeria");
            etPictogramLink.setEnabled(false);
        } else if (passedImageUrl != null) {
            etPictogramLink.setText(normalizeImageUrl(passedImageUrl));
            etPictogramLink.setEnabled(false);
        }

        btnSavePictogram.setOnClickListener(v -> {
            String name = etPictogramName.getText().toString().trim();
            String category = formatCategory(etPictogramCategory.getText().toString());

            String rawColor = etPictogramBorderColor.getText().toString().trim();
            String borderColor = rawColor.isEmpty() ? null : normalizeColor(rawColor);

            String link = "";
            String imageBase64 = "";

            boolean hasLocalImage = localImageUriString != null && !localImageUriString.trim().isEmpty();

            if (hasLocalImage) {
                imageBase64 = convertLocalImageToBase64(localImageUriString);

                if (imageBase64 == null || imageBase64.trim().isEmpty()) {
                    Toast.makeText(this, "Erro ao processar a imagem da galeria.", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                link = normalizeImageUrl(etPictogramLink.getText().toString().trim());
            }

            if (name.isEmpty()) {
                Toast.makeText(this, "Informe o nome do pictograma", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!hasLocalImage && link.isEmpty()) {
                Toast.makeText(this, "Informe o link da imagem ou selecione uma imagem da galeria", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.isEmpty()) {
                Toast.makeText(this, "Informe a categoria do pictograma", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!rawColor.isEmpty() && borderColor == null) {
                Toast.makeText(this, "Informe uma cor válida. Exemplo: vermelho, azul ou #FF0000", Toast.LENGTH_LONG).show();
                return;
            }

            if (tableId == null) {
                Toast.makeText(this, "Erro: Tabela não identificada.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSavePictogram.setEnabled(false);
            btnSavePictogram.setText("Verificando categoria...");

            validateCategoryColorAndSave(name, link, imageBase64, category, borderColor);
        });
    }

    private void validateCategoryColorAndSave(String name, String link, String imageBase64, String category, String borderColor) {
        FirebaseFirestore.getInstance().collection("pictograms")
                .whereEqualTo("tableId", tableId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    String newCategoryKey = categoryKey(category);

                    String existingColorForSameCategory = null;
                    String existingCategoryName = null;
                    String categoryUsingTypedColor = null;

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String existingCategory = doc.getString("category");
                        String existingColor = doc.getString("borderColor");

                        if (existingCategory == null || existingColor == null) {
                            continue;
                        }

                        String existingCategoryFormatted = formatCategory(existingCategory);
                        String existingCategoryKey = categoryKey(existingCategoryFormatted);
                        String existingColorNormalized = normalizeColor(existingColor);

                        if (existingColorNormalized == null) {
                            continue;
                        }

                        boolean sameCategory = existingCategoryKey.equals(newCategoryKey);

                        if (sameCategory && existingColorForSameCategory == null) {
                            existingColorForSameCategory = existingColorNormalized;
                            existingCategoryName = existingCategoryFormatted;
                        }

                        if (!sameCategory && borderColor != null && existingColorNormalized.equalsIgnoreCase(borderColor)) {
                            categoryUsingTypedColor = existingCategoryFormatted;
                        }
                    }

                    String resolvedBorderColor = borderColor;

                    if (existingColorForSameCategory != null) {
                        if (resolvedBorderColor == null) {
                            resolvedBorderColor = existingColorForSameCategory;
                        } else if (!resolvedBorderColor.equalsIgnoreCase(existingColorForSameCategory)) {
                            resetSaveButton();
                            Toast.makeText(
                                    this,
                                    "A categoria " + existingCategoryName + " já usa a cor " + existingColorForSameCategory + ". Deixe o campo vazio ou use essa mesma cor.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        savePictogram(name, link, imageBase64, category, resolvedBorderColor);
                        return;
                    }

                    if (resolvedBorderColor == null) {
                        resetSaveButton();
                        Toast.makeText(
                                this,
                                "Essa é a primeira vez que a categoria " + category + " será usada. Informe uma cor para ela.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    if (categoryUsingTypedColor != null) {
                        resetSaveButton();
                        Toast.makeText(
                                this,
                                "A cor " + resolvedBorderColor + " já está sendo usada pela categoria " + categoryUsingTypedColor + ". Escolha outra cor.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    savePictogram(name, link, imageBase64, category, resolvedBorderColor);
                })
                .addOnFailureListener(e -> {
                    resetSaveButton();
                    Toast.makeText(this, "Erro ao validar categoria: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void savePictogram(String name, String link, String imageBase64, String category, String borderColor) {
        btnSavePictogram.setText("Adicionando...");

        Map<String, Object> pictogram = new HashMap<>();
        pictogram.put("name", name);
        pictogram.put("imageUrl", link);
        pictogram.put("imageBase64", imageBase64);
        pictogram.put("imageMimeType", imageBase64 == null || imageBase64.isEmpty() ? "" : "image/jpeg");
        pictogram.put("tableId", tableId);
        pictogram.put("category", category);
        pictogram.put("borderColor", borderColor);

        FirebaseFirestore.getInstance().collection("pictograms")
                .add(pictogram)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Pictograma adicionado!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    resetSaveButton();
                    Toast.makeText(this, translateError(e), Toast.LENGTH_SHORT).show();
                });
    }

    private String convertLocalImageToBase64(String uriText) {
        try {
            Uri uri = Uri.parse(uriText);

            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            if (inputStream != null) {
                inputStream.close();
            }

            if (originalBitmap == null) {
                return null;
            }

            Bitmap resizedBitmap = resizeBitmapKeepingRatio(originalBitmap, 500);
            Bitmap whiteBackgroundBitmap = addWhiteBackground(resizedBitmap);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            whiteBackgroundBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            outputStream.close();

            originalBitmap.recycle();

            if (resizedBitmap != originalBitmap) {
                resizedBitmap.recycle();
            }

            if (whiteBackgroundBitmap != resizedBitmap) {
                whiteBackgroundBitmap.recycle();
            }

            return Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        } catch (Exception e) {
            return null;
        }
    }

    private Bitmap resizeBitmapKeepingRatio(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxSize && height <= maxSize) {
            return bitmap;
        }

        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private Bitmap addWhiteBackground(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return newBitmap;
    }

    private void resetSaveButton() {
        btnSavePictogram.setEnabled(true);
        btnSavePictogram.setText("Salvar pictograma");
    }

    private String normalizeImageUrl(String rawUrl) {
        if (rawUrl == null) return "";

        String url = rawUrl.trim();

        if (url.isEmpty()) {
            return "";
        }

        String arasaacId = extractArasaacId(url);

        if (arasaacId != null) {
            return buildArasaacDirectImageUrl(arasaacId);
        }

        return url;
    }

    private String extractArasaacId(String url) {
        if (url == null) return null;

        Pattern pagePattern = Pattern.compile("/pictograms/[^/]+/(\\d+)");
        Matcher pageMatcher = pagePattern.matcher(url);

        if (pageMatcher.find()) {
            return pageMatcher.group(1);
        }

        Pattern apiPattern = Pattern.compile("/api/pictograms/(\\d+)");
        Matcher apiMatcher = apiPattern.matcher(url);

        if (apiMatcher.find()) {
            return apiMatcher.group(1);
        }

        return null;
    }

    private String buildArasaacDirectImageUrl(String id) {
        return "https://static.arasaac.org/pictograms/" + id + "/" + id + "_300.png";
    }

    private String formatCategory(String rawCategory) {
        if (rawCategory == null) return "";

        String text = rawCategory.trim();
        if (text.isEmpty()) return "";

        String key = text.toLowerCase(Locale.ROOT);

        switch (key) {
            case "verbo":
            case "verbos":
                return "Verbo";

            case "substantivo":
            case "substantivos":
                return "Substantivo";

            case "adjetivo":
            case "adjetivos":
                return "Adjetivo";

            case "pronome":
            case "pronomes":
                return "Pronome";

            case "pessoa":
            case "pessoas":
                return "Pessoa";

            case "lugar":
            case "lugares":
                return "Lugar";

            case "alimento":
            case "alimentos":
                return "Alimento";

            case "sentimento":
            case "sentimentos":
                return "Sentimento";

            case "rotina":
            case "rotinas":
                return "Rotina";

            default:
                return capitalizeWords(text);
        }
    }

    private String categoryKey(String category) {
        if (category == null) return "";
        return formatCategory(category).toLowerCase(Locale.ROOT).trim();
    }

    private String capitalizeWords(String text) {
        String[] words = text.toLowerCase(Locale.ROOT).trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            if (result.length() > 0) {
                result.append(" ");
            }

            result.append(word.substring(0, 1).toUpperCase(Locale.ROOT));

            if (word.length() > 1) {
                result.append(word.substring(1));
            }
        }

        return result.toString();
    }

    private String normalizeColor(String rawColor) {
        if (rawColor == null) return null;

        String colorText = rawColor.trim();

        if (colorText.isEmpty()) return null;

        String key = colorText.toLowerCase(Locale.ROOT);

        switch (key) {
            case "vermelho":
                return "#F44336";
            case "azul":
                return "#2196F3";
            case "verde":
                return "#4CAF50";
            case "amarelo":
                return "#FFEB3B";
            case "laranja":
                return "#FF9800";
            case "roxo":
                return "#9C27B0";
            case "lilás":
            case "lilas":
                return "#B39DDB";
            case "rosa":
                return "#E91E63";
            case "marrom":
                return "#795548";
            case "cinza":
                return "#9E9E9E";
            case "preto":
                return "#000000";
            case "branco":
                return "#FFFFFF";
        }

        if (colorText.matches("^[0-9a-fA-F]{6}$")) {
            colorText = "#" + colorText;
        }

        try {
            int parsedColor = Color.parseColor(colorText);
            return String.format(Locale.ROOT, "#%06X", (0xFFFFFF & parsedColor));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}