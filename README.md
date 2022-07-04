# SpringBootWebServiceHomework
### ÖDEV TESLİM TARİHİ : 01/07/2022 Cuma 18:00

# Merhalar Arkadaşlar
Bu ödevimizde spring boot kullanarak, üç adet webservis geliştireceğiz. 

Ödevimiz kullanıcıların;
* Fimlere dair ismine göre arama yapıp detaylarını alabileceği, 
* Filmi listesine kaydedebileceği(Kullanıcıları ayırt etme nasıl olacak gibi bir senaryo düşünmeyin şu an sadece bir kullanıcı var onun için yapıyoruz bu işi),
* Direk olarak id'si ile bir filmin detayını isteyebileceği

3 adet webservis geliştireceğiz.

Bunları yaparken ilk başta bizde herhangi bir film bilgisi olmadığı için bu bilgilere sahip dış bir werservisten bu bilgileri almamız gerekecek. Buna dair bilgiyi sonda vereceğim arkadaşlar. 

**İlk Webservisimiz**

* Kullanıcıdan filmin adını parametre olarak alacaktır ve filmlerin bilgisi bize verebilen dış bir webservisi kullanarak bu filme(veya filmlere) dair bilgileri kullanıcıya cevap olarak dönecektir. Bu servis bilgiyi her zaman dış servisten alacaktır.
* Bu servis film adı parametresini query string olarak alacaktır. Query string HTTP protokolünde sunucu'ya parametre geçirme yöntemlerinden biridir ve parametre URL'in sonuna ?movie_name=Interstellar şeklinde key=value şeklinde eklenmektedir.
* Spring boot tarafında bu parametre @RequestParam annotationu kullanılarak direk methoda parametre olarak alınabilmektedir.
* Spring boot tarafında direk olarak JSON cevap vermek için(webservis geliştirdiğimiz için tercihimiz JSON olacak) @Controller annotationu yerine @RestController annotationu kullanabiliriz. @RestContoller; @Controller ve @ResponseBody annotationunun birleşmiş halidir. Bu sayede framework return ettiğiniz şey için view aramaz, direk kullanıcıya cevap döner.
```
    @RestController
    public class MovieController{
        @GetMapping("/movies/search")
        public List<Movie> search(@Requestparam(name = "movie_name") String movieName){
            //operations
        }
    }
```    

**İkinci Webservisimiz**
* Kullanıcıdan filmin id'sini alacak ve bu filmi ilerde dış bir servise gitmemek üzere bir dosyaya kaydedecektir.(Tüm detayları ile) Daha sonraki detay isteklerinde önce bu dosyayı kontrol edeceğiz film var mı diye varsa dosyadan okuyarak vereceğiz. Eğer burada yoksa dış servise istek atacağız.
* Kullanacağımız servis film ID'lerini String olarak belirlediği için bizde string olarak okuyacağız parametre'yi
* Burada Film id'si path parametresi olarak alınacaktır. Path parametresi HTTP'de diğer bir veri gönderme yöntemidir ve URL'e /[id'nin değeri] şeklinde /'dan sonra eklenerek gönderilir, java tarafında bunu @PathVariable annotationu kullanarak okuyabilmekteyiz. Mesela filmin id'si tt12345 olsun bunun için istek /movies/detail/tt12345 şeklinde olacaktır.
* Bu servis aldığı film id'sine göre dış servise istek atacak ve detayı alıp bir dosyaya satır satır kaydedecektir.
* Bu servis HTTP POST methodu ile çalışmalıdır.
```
    @RestController
    public class MovieController{
        @PostMapping("/movies/saveToList/{id}")
        public boolean addToList(@PathVariable(name = "id") Stirng id){
            //operations
        }
    }
```    
    
**Üçüncü Webservisimiz**
* Bu webservisimiz filmin detayını getiren servisimiz olacaktır. 
* Kullanıcı buraya filmin id'si ile istek atacaktır. 
* Biz öncelikle kaydettiğimiz dosyada bu filme dair detay var mı diye bakacağız. 
* Eğer dosyamızda bulursak dosyadan filmin detayını okuyup kullanıcıya cevap olarak döneceğiz.
* Eğer dosyada yoksa dış servisten veriyi isteyeceğiz. Ve kullanıcıya cevap olarak bunu döneceğiz.
* Filmin id'sini bir önceki serviste aynı şekilde path parametresi olarak alacağız.
```
    @RestController
    public class MovieController{
        @PostMapping("/movies/detail/{id}")
        public Movie detail(@PathVariable(name = "id") String id){
            //operations
        }
    }
```    
    
## Notlar

1. Arkadaşlar bir peoje geliştirirken çoğuz aman dış bir kaynaktan bir şey okumamız gerekebiliyor. Burada sizinde dış bir kaynaktan filmlere dair veri almanız gerekiyor. Bunun için [Collect Api](https://collectapi.com/api/imdb/imdb-api) adresinde bulunan IMDB webservisinizi kullanabilirsiniz. Nasıl kullanacağınıza dair orda detaylı bilgiler mevcuttur arkadaşlar.
2. Java kodunda dış bir webservisi çağırabilmek için çok fazla opsiyona sahibiz arkadaşlar. Spring dünyasında ise default olarak RestTemplate adını verdiğimiz bir kütüphane kullanılabilmektedir. Bu spring bootun içinde gelmektedir eğer spring-web bağımlılığı pom'unuzda varsa.
```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <scope>test</scope>
    </dependency>
```    
3. Örnek bir restTemplate kullanılımı şu şekildedir. Direk olarak JSON cevapları objeye çevirebilmektedir bu kütüphane, bunu araştırıp bulmanızı istiyorum arkadaşlar. Eğer direk aldığınız cevabı objeye çevirmeden JSON stringi üstünden işlem yapacaksınız bunun için GSON, json-simple gibi kütüphaneleri kullanabiliriz direk pom.xml'e ekleyerek.
```
   RestTemplate restTemplate = new RestTemplate();
   String result = restTemplate.getForObject("https://api.collectapi.com/imdb/imdbSearchByName?query=inception", String.class);
```   
4. Dış servisin döndüğü cevabın bir örneği şu şekildedir dosyaya sadece bu bilgileri kaydetmeniz yetecektir.
```
   {
      "Title": "Inception",
      "Year": "2010",
      "imdbID": "tt1375666",
      "Type": "movie",
      "Poster": "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg"
    },
```    
5. İşin belkide en önemli kısmı dosyaya kaydetme ve okuma kısmıdır. Burada sizin hızlıca filmi bulabilmek için bir kayıt formatına karar vermeniz lazım, ödevi ben yapıyor olsam şu şekilde bir çözümüm olabilirdi. Dosyaya satır satır kayıt yap, filmin bilgilerini aralarında ,(virgül) olacak şekilde kaydet, birde her satırda ilk sıraya id'yi koy ki bulurken daha hızlı bulabilesin.

```
tt1375666,Inception,2010,movie,ttps://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg
tt1375643,Interstellar,2010,movie,ttps://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX301.jpg
```

şeklinde. Detay isteği geldiğinde satır satır okuma yapıp id karşılaştırma yapıp filmi bulmaya çalışırdım!
!!**Webservislerinizi test etmek için POSTMAN adını verdiğimiz HTTP istekleri atabilen yazılımı kullanabilirsiniz.**!!

# BONUS
Arkadşalar veri kaynağının iki tane olduğu, bunların biribirne benzediğin düşünüp interface geliştirenler bonus kazanacaktır. Bu bonusun henüz ne oldu belli değildir, zamanla karar verilecektir:)

