# 🔒 LockBox

LockBox is an open-source, secure file storage solution designed to provide maximum security for storing and managing files online. Built with Spring Boot, this application leverages state-of-the-art encryption and a robust authentication framework to ensure that your data remains secure and private.



# ⭐ Features

- **End-to-End Encryption** 🔐 - Files are encrypted before being uploaded, ensuring that they are accessible only by the owner.
- **JWT Authentication** 👤 - Secure and stateless user authentication using JSON Web Tokens (JWTs).
- **Audit Trails** 📜 - Detailed logging for file access and downloads, with options for toggling logs.
- **PostgreSQL Integration** 🐘 - Efficient management of user data and metadata with PostgreSQL.
- **Spring Security** 🛡️ - Protection against common security threats and vulnerabilities.

## 🔧 Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/JakobRangel/LockBox.git

2. **Navigate to project directory:**
   ```bash
   cd LockBox
3. **Install Dependencies:**
   ```bash
   ./mvnw install
4. **Configure PostgreSQL:**
    ```bash
   Update src/main/resources/application.properties with your database credentials.
6. **Run Application:**
   ```bash
   ./mvnw spring-boot:run
# 💻 Usage

After launching the application, navigate to the URL where LockBox is hosted. If you are running it locally, this will typically be `http://localhost:8080`. If LockBox is deployed on a remote server, use the server's IP address or domain name (e.g., `http://yourdomain.com`). From there, you can register a new account or log in to begin securely uploading and managing your files.


# 👥 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.


Fork the Project

Create your Feature Branch `(git checkout -b feature/AmazingFeature)`

Commit your Changes `(git commit -m 'Add some AmazingFeature')`

Push to the Branch `(git push origin feature/AmazingFeature)`

Open a Pull Request

# 📝 License

Distributed under the GPL v3.0 License. See LICENSE for more information.

# 🙌 Acknowledgements

- Spring Boot
- PostgreSQL
- JWT
- Spring Security

Note: This script is intended for personal and educational use only. Use it responsibly and ensure compliance with Kick's terms of service. We are not responsible for any misuse of this script.
