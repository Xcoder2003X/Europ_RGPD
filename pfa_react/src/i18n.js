import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

// Translation files
const resources = {
  en: {
    translation: {
      login: "Login",
      username: "Username",
      nav_btn1: "Get Started",
      nav_btn2: "Secure Login",
      password: "Password",
      register: "Register",
      signupMessage: "Signup now and get full access to our app.",
      role: "Role",
      user: "User",
      admin: "Admin",
      alreadyHaveAccount: "Already have an account?",
      submit: "Submit",
      fileUpload: {
        title: "Upload Your Files",
        description: "Select a file to upload to the server.",
        uploadButton: "Upload",
        analyzeButton: "Analyze the file 🧠🧠",
        generateReportButton: "Generate Report 📎📎",
        validConsent: "✅ Valid consent",
        invalidConsent: "⛔ Invalid consent",
        generatedReportTitle: "Generated Report",
        reportSuccessMessage: "The Report was generated successfully.",
        downloadPrompt: "You can download it below :",
        closeButton: "Close",
        downloadButton: "Download",
        errorMessage: "Error: {{error}}",
      },
      firstSteps: {
        title: "Automated GDPR Analysis",
        title1: "Get started in a few simple steps.",
        step1: "Select your file",
        step2: "Automatic analysis of your data",
        step3: "Detection of GDPR non-compliance",
        step4: "Detailed report with recommendations",
        getStartedButton: "Start the analysis",
      },
    },
  },
  fr: {
    translation: {
      login: "Connexion",
      username: "Nom d'utilisateur",
      password: "Mot de passe",
      register: "S'inscrire",
      nav_btn1: "Commencer",
      nav_btn2: "Connexion sécurisée",

      signupMessage: "Inscrivez-vous maintenant et accédez à toutes les fonctionnalités de notre application.",
      role: "Rôle",
      user: "Utilisateur",
      admin: "Administrateur",
      alreadyHaveAccount: "Vous avez déjà un compte?",
      submit: "Soumettre",
      fileUpload: {
        title: "Téléchargez vos fichiers",
        description: "Sélectionnez un fichier à télécharger sur le serveur.",
        uploadButton: "Télécharger",
        analyzeButton: "Analyser le fichier 🧠🧠",
        generateReportButton: "Générer le rapport 📎📎",
        validConsent: "✅ Consentement valide",
        invalidConsent: "⛔ Consentement invalide",
        generatedReportTitle: "Rapport généré",
        reportSuccessMessage: "Le rapport a été généré avec succès.",
        downloadPrompt: "Vous pouvez le télécharger ci-dessous :",
        closeButton: "Fermer",
        downloadButton: "Télécharger",
        errorMessage: "Erreur : {{error}}",
      },
      firstSteps: {
        title: "Analyse RGPD Automatisée",
        title1: "Commencez en quelques étapes simples.",
        step1: "Sélectionnez votre fichier",
        step2: "Analyse automatique de vos données",
        step3: "Détection des non-conformités RGPD",
        step4: "Rapport détaillé avec recommandations",
        getStartedButton: "Démarrer l’analyse",
      },
    },
  },
  ar: {
    translation: {
      login: "تسجيل الدخول",
      username: "اسم المستخدم",
      password: "كلمة المرور",
      register: "تسجيل",
      nav_btn1: "ابدأ الآن",
      nav_btn2: "تسجيل دخول آمن",

      signupMessage: "سجل الآن واحصل على وصول كامل إلى تطبيقنا.",
      role: "الدور",
      user: "مستخدم",
      admin: "مسؤول",
      alreadyHaveAccount: "هل لديك حساب بالفعل؟",
      submit: "إرسال",
      fileUpload: {
        title: "قم بتحميل ملفاتك",
        description: "اختر ملفًا لتحميله إلى الخادم.",
        uploadButton: "تحميل",
        analyzeButton: "حلل الملف 🧠🧠",
        generateReportButton: "إنشاء التقرير 📎📎",
        validConsent: "✅ موافقة صالحة",
        invalidConsent: "⛔ موافقة غير صالحة",
        generatedReportTitle: "التقرير المُنشأ",
        reportSuccessMessage: "تم إنشاء التقرير بنجاح.",
        downloadPrompt: "يمكنك تنزيله أدناه :",
        closeButton: "إغلاق",
        downloadButton: "تنزيل",
        errorMessage: "خطأ: {{error}}",
      },
      firstSteps: {
        title: "تحليل RGPD تلقائي",
        title1: "	ابدأ في خطوات بسيطة.",
        step1: "اختر ملفك",
        step2: "تحليل تلقائي لبياناتك",
        step3: "كشف عن عدم التوافق مع RGPD",
        step4: "تقرير مفصل مع توصيات",
        getStartedButton: "ابدأ التحليل",
      },
    },
  },
};

i18n
  .use(LanguageDetector) // Detects the user's language
  .use(initReactI18next) // Passes i18n instance to react-i18next
  .init({
    resources,
    fallbackLng: 'en', // Default language
    interpolation: {
      escapeValue: false, // React already escapes values
    },
  });

export default i18n;