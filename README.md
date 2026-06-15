# Authority – Secure Serverless P2P Chat

**Authors:** João Marques (`@Yvtq8K3n`), João Jorge (`@cloropower`)  
**Institution:** Instituto Politécnico de Leiria, Portugal  
**Supervisors:** Prof. Mário Antunes, Prof. Luís Frazão  

## Disclaimer
This project was developed in 2020 for academic purposes only as part of a curricular unit. It is not maintained, and it aims to be a proof‑of‑concept for anonymous P2P communications.

## Overview
Pure peer‑to‑peer chat application for LAN environments with end‑to‑end encryption. No central server. Implements automatic peer discovery (UDP broadcast + TCP unicast) and hybrid cryptography (RSA for handshake, AES for messaging).

## Screenshot

![Chat working](https://github.com/Yvtq8K3n/Authority/blob/master/latex_template/images/teste_chat_funcional.png)

## Functionality
- ✅ LAN peer discovery (firewall dependent)
- ✅ Secure channel establishment (RSA + AES)
- ✅ Encrypted real‑time messaging
- ❌ File transfer (not completed)
- ❌ Internet‑scale operation (LAN only)
- ❌ Mutual authentication / mTLS

## Technologies
Java (JDK 8+), UDP/TCP sockets, Java Cryptography Architecture (RSA/AES)

## Original Paper
`Authority___Secure_Serverless_P2P_Chat.pdf`.
