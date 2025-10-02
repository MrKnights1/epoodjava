# Kasutajaliidese kasutusjuhend

## Rakenduse käivitamine

```bash
./run.fish
# või
java -jar target/order-system-1.0-SNAPSHOT.jar
```

## Peamenüü

Rakendus käivitub interaktiivse menüüga:

```
╔════════════════════════════════════════════════════════════╗
║    🛍️  E-POE TELLIMUSTE SÜSTEEM  🛍️                       ║
╚════════════════════════════════════════════════════════════╝

📋 PEAMENÜÜ:
  [1] 🛒 Loo uus tellimus
  [2] 📦 Sirvi tooteid
  [3] 📜 Vaata tellimuste ajalugu
  [4] 💰 Vaata teenuste hinnad
  [5] ℹ️  Info rakenduse kohta
  [0] 🚪 Välju
```

## 1️⃣ Uue tellimuse loomine

### Samm 1: Vali toode kataloogist

Süsteem näitab saadaolevaid tooteid:

```
🛒 TOOTEKATALOOG
────────────────────────────────────────────────────────────

[1] 💻 Sülearvuti Lenovo ThinkPad
    💰 899.99€
    📝 Professionaalne 14" sülearvuti, Intel i5, 16GB RAM

[2] 📱 Nutitelefon Samsung Galaxy
    💰 599.00€
    📝 6.1" AMOLED ekraan, 128GB, 5G toega

[3] 🎧 Juhtmevabad kõrvaklapid Sony
    💰 179.99€
    📝 Mürasummutusega, 30h aku, Bluetooth 5.0

[4] 📚 Raamat 'Clean Code'
    💰 45.50€
    📝 Robert C. Martin, programmeerimise klassika

[5] ☕ Kohvimasin DeLonghi
    💰 299.00€
    📝 Automaatne espressomasin, integreeritud kohviveski

[0] ❌ Tühista

Vali toode (number): _
```

### Samm 2: Lisa lisateenuseid

Pärast toote valimist saad lisada lisateenuseid:

```
🎁 LISA LISATEENUSEID
────────────────────────────────────────────────────────────

Praegune tellimus: 📱 Nutitelefon Samsung Galaxy
Praegune hind: 599.00€

📦 Saadaolevad teenused:
  [1] 🎁 Kingituspakend (+5.00€)
  [2] 🚀 Kiirtoimetamine (+10.00€)
  [3] 💌 Tervituskaart (+2.00€)
  [0] ✔️  Lõpeta teenuste lisamine

Mida soovid lisada? _
```

### Samm 3: Tellimus valmis!

```
═══════════════════════════════════════════════════════════
✅ TELLIMUS LOODUD!
═══════════════════════════════════════════════════════════
📝 Tellimus: 📱 Nutitelefon Samsung Galaxy + Kingituspakend + Kiirtoimetamine + Tervituskaart
💰 KOGUSUMMA: 616.00€
═══════════════════════════════════════════════════════════
```

## 2️⃣ Toodete sirvimine

Vaata kõiki saadaolevaid tooteid ilma tellimust loomata.

## 3️⃣ Tellimuste ajalugu

Näitab kõiki loodud tellimusi koos statistikaga:

```
📜 TELLIMUSTE AJALUGU
────────────────────────────────────────────────────────────

🛒 Tellimus #1
   📝 📱 Nutitelefon Samsung Galaxy + Kingituspakend + Kiirtoimetamine
   💰 614.00€

🛒 Tellimus #2
   📝 ☕ Kohvimasin DeLonghi + Kingituspakend
   💰 304.00€

────────────────────────────────────────────────────────────
📊 Tellimusi kokku: 2
💵 Käive kokku: 918.00€
💰 Keskmine tellimus: 459.00€
```

## 4️⃣ Teenuste hinnakiri

```
💰 TEENUSTE HINNAKIRI
────────────────────────────────────────────────────────────

🎁 Kingituspakend .................... 5.00€
   Ilus pakendikott või paber koos paelaga

🚀 Kiirtoimetamine .................. 10.00€
   Kaup jõuab kohale 1-2 tööpäevaga

💌 Tervituskaart ..................... 2.00€
   Personaliseeritud tervituskaart sinu sõnumiga

────────────────────────────────────────────────────────────
ℹ️  Kõiki teenuseid saab kombineerida!
ℹ️  Näiteks: Kõik 3 teenust kokku = 17.00€
```

## 5️⃣ Info rakenduse kohta

Näitab rakenduse versiooni, arhitektuuri ja kasutatud tehnoloogiaid.

## Näidiskasutus

### Stsenaarium 1: Kingitus sõbrale

1. Vali **[1] Loo uus tellimus**
2. Vali **[3] 🎧 Juhtmevabad kõrvaklapid Sony** (179.99€)
3. Lisa **[1] 🎁 Kingituspakend** (+5.00€)
4. Lisa **[3] 💌 Tervituskaart** ja sisesta sõnum: "Head sünnipäeva!"
5. Vali **[0] ✔️ Lõpeta**
6. **Kogusumma: 186.99€**

### Stsenaarium 2: Kiire kohaletoimetamine

1. Vali **[1] Loo uus tellimus**
2. Vali **[1] 💻 Sülearvuti Lenovo ThinkPad** (899.99€)
3. Lisa **[2] 🚀 Kiirtoimetamine** (+10.00€)
4. Vali **[0] ✔️ Lõpeta**
5. **Kogusumma: 909.99€**

### Stsenaarium 3: Premium kingituspakk

1. Vali **[1] Loo uus tellimus**
2. Vali **[2] 📱 Nutitelefon Samsung Galaxy** (599.00€)
3. Lisa **[1] 🎁 Kingituspakend** (+5.00€)
4. Lisa **[2] 🚀 Kiirtoimetamine** (+10.00€)
5. Lisa **[3] 💌 Tervituskaart** ja sisesta sõnum: "Õnnitlused uue ametikoha puhul!"
6. Vali **[0] ✔️ Lõpeta**
7. **Kogusumma: 616.00€**

## Saadaolevad tooted

| ID | Toode | Hind | Kirjeldus |
|----|-------|------|-----------|
| 1 | 💻 Sülearvuti Lenovo ThinkPad | 899.99€ | Professionaalne 14" sülearvuti |
| 2 | 📱 Nutitelefon Samsung Galaxy | 599.00€ | 6.1" AMOLED, 128GB, 5G |
| 3 | 🎧 Juhtmevabad kõrvaklapid Sony | 179.99€ | Mürasummutus, 30h aku |
| 4 | 📚 Raamat 'Clean Code' | 45.50€ | Programmeerimise klassika |
| 5 | ☕ Kohvimasin DeLonghi | 299.00€ | Automaatne espressomasin |

## Lisateenused

| Teenus | Hind | Kirjeldus |
|--------|------|-----------|
| 🎁 Kingituspakend | +5.00€ | Ilus pakendikott või paber |
| 🚀 Kiirtoimetamine | +10.00€ | Kohaletoimetamine 1-2 päevaga |
| 💌 Tervituskaart | +2.00€ | Personaliseeritud sõnumiga |

## Välju rakendusest

Vali peamenüüst **[0] 🚪 Välju**

---

💡 **Näpunäide:** Kõiki lisateenuseid saab kombineerida! Proovi erinevaid kombinatsioone.
