# Scripts Directory

This directory contains scripts and utilities to manage keys and certificates for JWT token signing and verification.

## Contents

- `keystore.jks`: The Java KeyStore file containing the RSA key pair.
- `publickey.cert`: The public key certificate exported from the keystore.
- `PublicKeyExtractor.java`: A Java program to extract the public key and generate a `jwks.json` file.
- `PublicKeyExtractor.kt`: A Kotlin version of the program to extract the public key and generate a `jwks.json` file.

## Steps to Generate Keys and Create JWKS File

### 1. Create the Keystore File

Use the following command to generate an RSA key pair and store it in a Java KeyStore (JKS) file:

```sh
keytool -genkeypair -alias test-alias -keyalg RSA -keysize 2048 -keystore keystore.jks -storetype JKS -validity 3650
```

You will be prompted to enter the keystore password and other details.

### 2. Export the Public Key Certificate

Use the following command to export the public key certificate from the keystore to a file:

```sh
keytool -exportcert -alias test-alias -keystore keystore.jks -rfc -file publickey.cert
```

This will create a file named `publickey.cert` containing the public key certificate in PEM format.

### 3. Run the PublicKeyExtractor to Generate JWKS

#### Using Java

1. Compile the `PublicKeyExtractor.java` file:

    ```sh
    javac scripts/PublicKeyExtractor.java
    ```

2. Run the `PublicKeyExtractor` program to generate the `jwks.json` file:

    ```sh
    java -cp scripts PublicKeyExtractor
    ```

#### Using Kotlin

1. Compile the `PublicKeyExtractor.kt` file (if you have a Kotlin version):

    ```sh
    kotlinc scripts/PublicKeyExtractor.kt -include-runtime -d PublicKeyExtractor.jar
    ```

2. Run the `PublicKeyExtractor` program to generate the `jwks.json` file:

    ```sh
    java -jar PublicKeyExtractor.jar
    ```

### 4. Copy the `jwks.json` File

After running the `PublicKeyExtractor`, the `jwks.json` file will be created in the `scripts` directory. Copy this file to your `src/main/resources` directory in your Micronaut project or upload it to a location accessible to your clients.

### Example `application.yml` Configuration

Make sure your `application.yml` file is configured to use the `jwks.json` file:

```yaml
micronaut:
  security:
    token:
      cookie:
        enabled: true
      jwt:
        enabled: true
        signatures:
          jwks-static:
            selfSigned:
              path: "jwks.json"
```

This setup ensures that your Micronaut application can load the JWKS configuration and use it to verify JWT tokens.

## Summary

1. **Create Keystore**: Generate an RSA key pair and store it in a JKS keystore.
2. **Export Public Key**: Export the public key certificate from the keystore.
3. **Generate JWKS**: Run the `PublicKeyExtractor` to create the `jwks.json` file.
4. **Copy JWKS**: Copy the `jwks.json` file to your `src/main/resources` directory or upload it to a location accessible to clients.
