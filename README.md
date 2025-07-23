
# 📌 Yük Testi Kurulum ve Çalıştırma Rehberi (Apache JMeter)

Bu proje için yük testi yapmak amacıyla **Apache JMeter** kullanılmaktadır.  
Aşağıdaki adımlar ile JMeter’ı kurabilir, test planını oluşturabilir ve yük testi senaryolarını çalıştırabilirsiniz.

---

## ✅ 1. JMeter Kurulumu

### 1.1. JMeter İndir  
- [Apache JMeter Resmi İndirme Sayfası](https://jmeter.apache.org/download_jmeter.cgi) adresine gidin.  
- **Binaries (.zip)** paketini indirin (örnek: `apache-jmeter-5.6.3.zip`).  

### 1.2. Kurulum  
- İndirilen dosyayı çıkarın (örnek: `C:\apache-jmeter-5.6.3`).  
- Java 8 veya üstü yüklü olduğundan emin olun:  
  ```bash
  java -version
  ```
- JMeter’ı başlatmak için:  
  - **Windows:** `bin/jmeter.bat`  
  - **Linux/Mac:** `bin/jmeter`  

---

## ✅ 2. Test Planı Yapılandırma

### 2.1. HTTP Request Defaults  
- **Server Name or IP:** `127.0.0.1`  
- **Port Number:** `8080`  
- **Protocol:** `http`  

### 2.2. HTTP Header Manager  
- **Content-Type:** `application/json`  

### 2.3. POST `/submit` İsteği  
- **Method:** `POST`  
- **Path:** `/submit`  
- **Body Data:**  
  ```json
  {
    "payload": {
      "name": "TestUser",
      "age": 30
    }
  }
  ```

### 2.4. GET `/status/{id}` İsteği  
- **Method:** `GET`  
- **Path:** `/status/1`  

---

## ✅ 3. Thread Group Ayarları  
- **Number of Threads (Users):** `10`  
- **Ramp-Up Period:** `5`  
- **Loop Count:** `2`  

---

## ✅ 4. Listener Ekleyin  
- **View Results Tree**  
- **Summary Report**  

---

## ✅ 5. Test Çalıştırma  
- JMeter GUI’de **Start (Yeşil Buton)** tıklayın.  
- Sonuçları **Summary Report** üzerinden analiz edin:  
  - **Throughput (RPS)**  
  - **Avg Response Time**  
  - **Error %**  

---

### 📌 Notlar  
- Test öncesinde **Spring Boot uygulamanın çalıştığından emin olun** (`http://127.0.0.1:8080`).  
- **Content-Type header’ını eklemeyi unutmayın**, aksi takdirde `415 Unsupported Media Type` hatası alırsınız.  
- Gelişmiş senaryolar için CSV Data Config kullanarak dinamik ID’ler ile GET isteği gönderebilirsiniz.  

---
