from bs4 import BeautifulSoup
import requests
from PyPDF2 import PdfReader
import re

class DataExtractor:
    @staticmethod
    def extract_from_url(url):
        try:
            response = requests.get(url)
            soup = BeautifulSoup(response.text, 'html.parser')
            # Nettoyage spécifique aux sites juridiques
            #Supprime les éléments HTML inutiles fréquents dans les sites juridiques :
            for element in soup(['footer', 'nav', 'script', 'style', 'header']):
                element.decompose()
            #Capture uniquement les balises utiles pour le contenu juridique :
            text = ' '.join([p.get_text() for p in soup.find_all(['p', 'article'])])
            return re.sub(r'\s+', ' ', text).strip()
        except Exception as e:
            print(f"Erreur sur {url} : {str(e)}")
            return ""

    @staticmethod
    def extract_from_pdf(pdf_url):
        try:
            response = requests.get(pdf_url)
            #Téléchargement du PDF
            with open("temp.pdf", "wb") as f:
                f.write(response.content)
            reader = PdfReader("temp.pdf")
            #Extraction du texte du PDF
            text = ''.join([page.extract_text() for page in reader.pages])
            return re.sub(r'\s+', ' ', text).strip()
        except Exception as e:
            print(f"Erreur PDF sur {pdf_url} : {str(e)}")
            return ""

# Liste de vos URLs
URLS = [
    "https://www.dpo-partage.fr/exemple-rapport-annuel-du-dpo/",
    "https://www.cnil.fr/fr/cybersecurite/les-violations-de-donnees-personnelles",
    "https://www.cnil.fr/sites/cnil/files/2024-03/cnil_guide_securite_personnelle_2024.pdf",
    "https://www.mission-rgpd.com/?utm_source",
    "https://reports.alpiq.com/20/fr/conformite-socio-economique/",
    "https://lecoursgratuit.com/rapport-rgpd-modele-excel-automatise/?utm_source",
    "https://www.witik.io/lp/modele-rapport-annuel-dpo/?utm_source"
    # Ajoutez toutes vos URLs ici
]

def run_data_pipeline():
    all_texts = []
    for url in URLS:
        if url.endswith('.pdf'):
            text = DataExtractor.extract_from_pdf(url)
        else:
            text = DataExtractor.extract_from_url(url)
        if text:
            all_texts.append(text)
    return all_texts