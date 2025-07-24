FROM python:3.12-slim

WORKDIR /app

# Gerekli kütüphaneleri yükle
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Python dosyasını kopyala
COPY main.py .

CMD ["python", "main.py"]
