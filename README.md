# LocalGuide 🌍
**AI-Powered C2C Tour Guide Platform**

LocalGuide Connect is a Consumer-to-Consumer (C2C) tour guide booking platform founded by a team of software engineering students in Canada. The platform addresses inefficiencies in the tourism industry by directly connecting travelers with verified local guides, reducing intermediary costs while maintaining high standards of trust and safety. 

## 🎯 Project Vision
Our mission is to build a transparent, AI-powered marketplace that benefits both travelers seeking authentic local experiences and professional guides who want to establish sustainable businesses without excessive platform fees.

## 💎 Core Value Propositions
- [cite_start]**Affordable Access**: Low commission model (12% vs industry standard 20-30%). 
- [cite_start]**Smart Discovery**: AI-powered search to reduce booking time from hours to minutes. 
- [cite_start]**Authentic Experiences**: Focus on hidden cultural treasures and local lifestyles. 
- [cite_start]**Verified Trust**: Transparent pricing and verified guide credentials for traveler safety.

## 📂 Project Documentation
For detailed information regarding our project requirements, business case, and development roadmap, please refer to the following resources:

- **📄 Project Proposal (Full Report)**: [LocalGuide-Group3-proposal.pdf](./docs/proposal/LocalGuide-Group3-proposal.pdf)
- **☁️ Requirements & Design Document**: [Google Docs Link](https://docs.google.com/document/d/1ccwN8AG62yl9nib6weKiWco2B2Lt0h56zSaBtOYWsWg/edit?tab=t.0)
- **🎥 Product Demo & Presentation**: [Online Video Link](https://drive.google.com/file/d/1kuOv6p57LE3pItfNREmhj3w_pTsPurky/view?usp=drive_link)

## 📅 Roadmap Overview
This project follows an **Agile/Scrum** methodology with a 12-week development timeline, focusing on delivering a Minimum Viable Product (MVP) that includes core features for tourists, guides, and administrators. 

## 🧩 Development Structure
To support parallel collaboration, the repository uses a module-based folder structure aligned with the 4 developer responsibilities:

```text
apps/backend/src/main/java/com/localguide/modules/
  auth-user/        -> Dev1 (Auth and User)
  guide-tour/       -> Dev2 (Guide, Tour, Search, Availability)
  booking-payment/  -> Dev3 (Booking, Payment, Notification)
  review-admin/     -> Dev4 (Review, Favorites, Admin)

apps/web/src/modules/
  auth-user/        -> Dev1
  guide-tour/       -> Dev2
  booking-payment/  -> Dev3
  review-admin/     -> Dev4
```

Module ownership details and collaboration rules are documented in:
- [docs/module-ownership.md](./docs/module-ownership.md)
- [docs/development-structure.md](./docs/development-structure.md)
