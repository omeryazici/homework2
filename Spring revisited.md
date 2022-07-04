# Revisit

Merhaba Arkadaşlar, geçen hafta işlediğimiz dersi ufak bir tekrar edecek olursak

Spring'in büyüyen projelerimizde yaşadığımız bazı sorunlara dair çözümler getiren bir  kütüphane oldugundan bahsettik.

Bu sorunlar

* Sınıfların birbiri ile çok iç-içe geçmesi(**Tight Coupling**)
* İlerde değişim ihtiyacı oldugunda çok fazla yere dokunulması gerektiği(**Refactoring, Code Changes**)
* Sınıflara fazla sorumluluk yüklenmesi(**Single Responsibility**)
* Kodumuzun test edilebilirliğinin zorlaşması(**Testing**)

**Spring** bu sorunların çözümü için nesnelerin hayat döngüsünü yönetebilmek, sınıfların bağımlı olduğu diğer sınıfları **DI(Dependency injection)** mentalitesi ile dışardan verilmesinin otomatize edilmesi gibi yeteneklere sahiptir.

    class Photographer(){
        private Camera camera;
        public Photographer(){
            this.camera = new Camera();
       }
    }

Örnekteki sınıfımız aslında yukarıda bahsettiğimi tüm sorunları bize gösteren bir sınıftır. Photographer sınıfı cameranın nasıl yaratılacağını bilmek zorunda bırakılmıştır, yani gereksiz sorumlulukları vardır.

Photographer sınıfı sadece belirli bir camera sınıfıyla çalışabilir haldedir.(**Tight Coupling**)

Bu sınıftaki koda dokunmadan Ptorographer'ın başka bir camera ile çalışması mümkün değildir. Halbuki bir photographer istedği anda başka kamera kullanabilmeli, hatta telefonunu kamera olarak kullanabilmelidir.

Photographer sınıfı test etmek istediğimde kesinlikle gerçek bir kamera dünyaya gelmelidir.

Bu sorunların hepsine aslında çok basit bir çözümümüz mevcuttur. **Bağımlıkların dışarıdan verilmesi!!** Yani Dependency Injection. Şimdi bu sınıfı şu hale getirirsek eğer

    class Photographer(){
        private Camera camera;
        public Photographer(Camera c){
           this.camera = c;
       }
       //Veya setter ile
      public void setCamera(Camera c){
          this.camera = c;
      }
    }

***Setter ve constructor*** ile bir sınıfın bağımlılıklarını dışardan verebiliriz OOP'de.

* Artık sınıfımız Cameranın nasıl yaratıldığını bilmek zorunda değildir.
* Photographer sınıfına Camera sınıfından extend etmek şartı ile farklı cameralar verebilirim.
* Camera sınıfında fake(mock) bir çocuk sınıf oluşturur test ederken onu kullanabilirim. 

Bunu biraz daha güzelleştirmek adına bir de interface'leri kullandık mı kodumuz ciddi anlamda esnek hale gelecektir.(**interface DI için bir şart değildir, DI bağımlılığı dışardan verip vermemenle alakalıdır, interface kullanmanla değil!**)

    Class Photographer(){
        private ICamera camera;
        public Photographer(ICamera c){
           this.camera = c;
       }
       //Veya setter ile
        public void setCamera(ICamera c){
           this.camera = c;
       }
    }
Artık bu sınıfa camera yollamak için extend etme şartın yoktur, ICamera interface'ini implement eden her sınıf buraya gönderilebilir!

İşte spring bize bu konularda yardımcı olan kütüphanemizdir. Ona belli yöntemler ile hayat döngüsünü, bağımlılıklarını yönetmesini istedğimiz sınıfları(Spring bunlara **bean** diyor) veriririz. O da bize lazım oldugunda güzelce bu işi yerine getirir.

Spring çok yeteneklidir, nesnenin doğumunda ölümüme, nasıl doğacağına, doğduktan sonra birşey yapılacak ise bunu yapmaya gibi konuların hepsine çözümleri vardır.(**Scope, Lazy-init, init-method, destroy-method**)

Bunu yaparken temelde 3 yöntemimiz vardır
* xml dosyası kullanarak bean'lerimizi tanımlamak
* **@Configuration** ve **@Bean** annotationları ile java kodu ile tanımlamak
* **Component Scanning** kullanarak **@Component** annotationu olan sınıfları bean olarak kaydetmek. **@Autowired** annotationu ilede bağımlılıkların otomatik inject edilmesini sağlamak.

Günümüzde yaygın olarak 2 ve 3 seçenekleri tercih edilmektedir.

Temelde bunları yapabilen bir kütüphane olarak geliştirilen spring, daha sonra JAVA ortamındaki bazı sorunları çözmek adına *Framework* olarak kullanılmaya başlamıştır. Java da bir WEB uygulaması geliştirirken temel olarak

* Tomcat web sunucusu
* Servlet ve JSP

kullanılmaktadır. Bir servlet(Web isteğine cevap verebilen bir java sınıfı) geliştirilip Tomcat'e bunun cevap vereceği URL'e dair bilgi verilip web sayfamız hazır hale getilebilmektedir. Örnek bir Servlet sınıfına baktığımızda

    public class HelloServlet extends HttpServlet {
    	private static final long serialVersionUID = 1L;
        public HelloServlet() {
            super();
        }
    	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	    response.getWriter().append("Hello World");
    	}
    }

şeklinde HttpServlet sınıfından extend eden, boş bir consturctora sahip, GET isteklerini karşılamak için doGet(request, response) şeklinde methoda sahip bir sınıf.

web.xml dosyası aracılığıyla buna bir URL verildikten sonra eğer o URL'e bir istek yapılırsa Tomcat bu sınıftan default(parametre almayan) constructorı ile bir nesne yaratıp, request ve response nesnelerini hazırlayıp doGet methodunu çağıracaktır. Herşey ne kadar güzel değil mi? Aksine bir çok sorun var bu mimari'de.

Benim bu sınıfta mesela kullanıcıları bir veri kaynağından okuma ihtiyacım oldugunu düşünün. Eğer test edilebilir, kolayca değiştirilebilir, tek bir sorumluluğa sahip bir sınıf yazmak istiyorsam bu veri kaynağında okuyan sınıfı buraya inject etmem gerekiyor. 

İşte sorun burda başlıyor, bu sınıfı ben yönetmiyorum! Ne yaratılması ne setterlarının çağırılması, ne de doGet methodunun çağırılması!

Constructora parametre eklesem tomcatin böyle bir yeteneği yok, nene yaratamaz.
setter eklesem tomcatin yine böyle bir iddiası ve yeteneği yok o methodu çağırmaz. Yani ben burda kendim new'lemek zorundayım, işte tam bu anda istemediğimiz bir noktaya geldik, bu kod ilerde başımıza bela olacaktır.

    public class HelloServlet extends HttpServlet {
    	private static final long serialVersionUID = 1L;
    	private Logger logger;
        public HelloServlet() {
            super();
        }
    	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	    response.getWriter().append("Hello World");
    	    this.logger = new Logger();
    	}
    }

# NOT
***Arkadaşlar sınıflarımızı DI kullanarak gerçekleştirmenin spring ile bir alakası yoktur, herhangi bir yerde herhangi bir sınıfımızı zaten böyle geliştirmek yukarda saydığımız faydalar için zorunluluktur. Kendimizde nesnelerimizi yaratırken bağımlıkları dışardan verebiliriz. Spring bize bir framework olarak geldiği için, bizim kodumuzu kendisi yaratıp yöneteceği için annotationlar ile ona ne vermesi gerekitğini söyleyen biziz. Yani Spring benim kodumu kullanmak istiyorsa zaten bunları yapmak zorunda!***

İşte tam bu noktada spring geliyor ve diyorki durun, bunu ben çözerim! Ve spring'in altın dönemi başlıyor. Sınıflarımızı, bağımlılıklarımızı springin yönettiği yeni bir WEB uygulama geliştirme yapısı doğmuş oluyor.

Peki nasıl?
Burada spring çok ince bir dokunuşla tüm web geliştirme altyapısını kendi yönettiği şekle getiriyor arkadaşlar.
Biz tomcate sadece özel, springin içinde bulunan bir servleti tüm istekleri karşılayacak şekilde kaydediyoruz. Buna **dispatcher servlet** diyoruz.

    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/beans.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

Bu servlete beanleri nasıl ne şekilde okuyacağını söylüyoruz.(**@Bean, @Component ve xml ile**)
Ve işte tam burda artık spring dunyasına geçiş yapıyoruz. Bundan sonra sınıflarımızı DI'a uygun geliştiriyoruz ve kodu spring yönettiği için @Autowire ile bağımlıklarımızı inject edilmesini sağlıyoruz. Bu sayede büyüyen projelerde değişimler, testler çok daha rahat yapılabilir hale geliyor.

Spring kütüphanesinin framework olma hikayesi işte burda başlıyor arkadaşlar. Spring'e web'e dair(Routing, MVC, Data katmanı, JPA) gibi şeyler eklenerek framework haline geliyor.
