# InformacionaBezbjednostProjekat

Napravljena je web aplikacija koja omogucava registraciju i prilikom registracije dodaje authority tipa regular.
Napravila sam klasu securityConfiguration koja konfigurise koji tip authoritija smije da ode na neku stranicu.
Pomocu Bcrypt enkodera svaka sifra prilikom registracije i prije upisa se hesira.
Odobravanje novih naloga od strane admina takodje radi.
Kreiranje jks datoteka prilikom registrovanja i onda se jsk i cer fajl skine u data.
Omogucena je pretraga i to uz pomoc javascripta.
Kada se korisnik uloguje i hoce da skine cer ili jks u controlleru se provjerava ko je ulogovan i salje se filename(po emailu su fajlovi dobijali ime)
i onda se provjerava da li je taj ulogovan korisnik isti kao taj proslijedjeni filename, ako nije skida se .cer, a ako jeste skida se njegov .jks.
Konekcija sa bazom je dodata u application properties i koristi se MySql baza.
Uspostavljena je komunikacija izmedju web app i https protokola.
Dodala sam novi jks i cer u data i stavila sam u application properties lokaciju do tog jksa, sifru i keystore type.

Kao admin se ulogujete: micy_98@gmail.com i sifra: 1234
Kao obican korisnik: joka@gmail.com i sifra: 1234

MailClient nije povezan sa ovom web aplikacijom nego kad se skine .cer  treba da se rucno importuje u zeljeni .jks u Portecle-u.
WriteMailClient-Unosi se prvo reciever,subject i body. Pravi se xml dokument, enkriptuje se uz pomoc javnog kljuca od korisnikaB, sacuva se u data kao poslataPoruka.xml. 
ReadMailClient-Ucitava se dokument, dekriptuje i save-uje. 
1234 - korisnikA
korisnikB- 4321
