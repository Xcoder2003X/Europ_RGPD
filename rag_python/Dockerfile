# Utilise une image Python officielle
FROM python:3.12-slim-bookworm
RUN pip install --upgrade pip && pip install waitress

# Définit le répertoire de travail
WORKDIR /app

# Installe les dépendances système nécessaires
RUN apt-get update && apt-get install -y \
    libmagic1 \
    poppler-utils \
    tesseract-ocr \
    libtesseract-dev \
    build-essential \
    python3-dev \
    && rm -rf /var/lib/apt/lists/*

# Copie les fichiers nécessaires
COPY requirements.txt .

# Installe les dépendances Python
RUN pip install --no-cache-dir -r requirements.txt

# Copie le reste de l'application en préservant la structure des dossiers
COPY app/ ./app/
COPY data_pipeline/ ./data_pipeline/
COPY rag_core/ ./rag_core/
COPY document_cache/ ./document_cache/
COPY vector_store/ ./vector_store/

# Expose le port 5000
EXPOSE 5000

# Commande de démarrage (nouveau format)
CMD ["python", "-m", "app.api"]