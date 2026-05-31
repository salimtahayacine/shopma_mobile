# Règles ProGuard par défaut

# Conserver les modèles utilisés par Gson/Retrofit
-keep class com.shopma.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
