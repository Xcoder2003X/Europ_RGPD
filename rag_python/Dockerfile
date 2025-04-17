FROM python:3.10-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# For GPU support (optional)
# FROM nvidia/cuda:12.2.0-base-ubuntu22.04
# ... (rest remains same)

EXPOSE 5000
CMD ["python", "app/api.py"]