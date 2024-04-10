/**
* Programlama Dillerinin Prensipleri Ahmet Zengin
* @author Feyza Ezber - feyza.ezber@ogr.sakarya.edu.tr
* @since 02.04.2024
* <p>
* Bilgisayar Mühendisliği 2. Sınıf, 2. Öğretim B grubu 
* </p>
*/


package project3;
import java.io.BufferedReader; //Büyük metin dosyalarını satır satır okumak için kullanılır.
import java.io.File; //Dosya ve dizinlerle ilgili işlemleri gerçekleştirmemizi sağlar.
import java.io.FileReader; //Karakter tabanlı dosyaları okumak için kullanılır.
import java.io.IOException; //Giriş/Çıkış işlemleri sırasında oluşabilecek hataları yakalamak için kullanılır.
import java.io.InputStreamReader; //Bayt tabanlı bir giriş akışını karakter tabanlı bir giriş akışına dönüştürür.
import java.util.ArrayList; //Elemanları sıralı bir şekilde tutan dinamik bir veri yapısıdır. Farklı türdeki verileri saklamak için kullanılabilir.
import java.util.List; //Elemanları sıralı bir şekilde tutan bir arayüzdür.

public class project3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
            // Bu kısımda kullanıcıdan GitHub deposu URL'sini alıyoruz.
            BufferedReader okuyucu = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("GitHub Deposu URL'si: ");
            String repoUrl = okuyucu.readLine();
            okuyucu.close();

            // Depoyu klonluyoruz
            ProcessBuilder klonlamaProcessBuilder = new ProcessBuilder("git", "clone", repoUrl);
            Process klonlamaProcess = klonlamaProcessBuilder.start();
            klonlamaProcess.waitFor();

            File repoDir = new File(getRepoName(repoUrl));
            if (!repoDir.exists()) {
                System.out.println("Depo klonlama başarısız oldu.");
                return;
            }
            System.out.println("Depo başarıyla klonlandı.");
            System.out.println("Depo analiz ediliyor...");

            // *.java uzantılı dosyaları buluyoruz
            List<File> javaDosyaları = findJavaDosyaları(repoDir);
            for (File javaDosyası : javaDosyaları) {
                dosyayıAnalizEt(javaDosyası);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Depo ismi
    private static String getRepoName(String repoUrl) {
        String[] parçalar = repoUrl.split("/");
        return parçalar[parçalar.length - 1].replace(".git", "");
    }

    // *.java dosyaları
    private static List<File> findJavaDosyaları(File dizin) {
        List<File> javaDosyaları = new ArrayList<>();
        File[] dosyalar = dizin.listFiles();
        if (dosyalar != null) {
            for (File dosya : dosyalar) {
                if (dosya.isDirectory()) {
                    javaDosyaları.addAll(findJavaDosyaları(dosya));
                } else if (dosya.getName().endsWith(".java")) {
                    javaDosyaları.add(dosya);
                }
            }
        }
        return javaDosyaları;
    }

    // Dosya analizini gerçekleştiriyoruz.
    private static void dosyayıAnalizEt(File dosya) {
        String dosyaAdı = dosya.getName();
        System.out.println("Dosya Adı: " + dosyaAdı);

        int javadocSatırSayısı = javadocSatırSayısınıHesapla(dosya);
        System.out.println("Javadoc Yorum Satırı Sayısı: " + javadocSatırSayısı);

        int digerYorumSatırSayısı = digerYorumSatırSayısınıHesapla(dosya);
        System.out.println("Diğer Yorum Satırı Sayısı: " + digerYorumSatırSayısı);

        int kodSatırSayısı = kodSatırlarınıSay(dosya);
        System.out.println("Kod Satır Sayısı: " + kodSatırSayısı);
        
        int locSayısı = locSatırlarınıSay(dosya);
        System.out.println("LOC (Line of Code) Sayısı: " + locSayısı);

        int fonksiyonSayısı = fonksiyonSayısınıSay(dosya);
        System.out.println("Fonksiyon Sayısı: " + fonksiyonSayısı);
        
       double yorumSapmaYuzdesi = yorumSapmaYuzdesiHesapla( javadocSatırSayısı,  digerYorumSatırSayısı,  kodSatırSayısı,  fonksiyonSayısı);
        System.out.println("Yorum Sapma Yüzdesi: " + yorumSapmaYuzdesi);

        System.out.println();
    }

    // Javadoc yorum satırı sayısını hesaplayan fonksiyon
    private static int javadocSatırSayısınıHesapla(File dosya) {
    	int satırSayısı = 0;
        boolean javadocIçinde = false;

        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
            String satır;
            while ((satır = okuyucu.readLine()) != null) {
                satır = satır.trim();

                // javadoc yorum satırı /** işareti ile başlar ve satırın sonunda bir işaret olmaması gerekir
                if (satır.startsWith("/**") && !satır.endsWith("*/")) {
                    javadocIçinde = true;
                    continue; // eğer javadoc içindeyse bu satırı saymıyoruz, bir sonraki satıra geçiyoruz
                }

                // Javadoc içindeyse ve satırın başında * işareti varsa
                if (javadocIçinde && (satır.startsWith("*") || satır.equals("*"))) {
                    // Boşlukları atlayarak sayıyoruz
                    if (!satır.substring(1).trim().isEmpty()) {
                        satırSayısı++; // Bu satırı javadoc olarak sayıyoruz
                    }
                }

                // eğer satır */ işaretini içeriyorsa javadoc yorum bloğu bitmiştir
                if (javadocIçinde && satır.contains("*/")) {
                    javadocIçinde = false;
                    continue; // Bu satırı saymıyoruz, bir sonraki satıra geçiyoruz
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return satırSayısı;
    }
    
    //javadoc haricindeki diğer yorumların satır sayısını hesaplayan fonksiyon
    private static int digerYorumSatırSayısınıHesapla(File dosya) {
    	int digerYorumSatırSayısı = 0;
        boolean yorumBloğuIçinde = false;

        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
            String satir;

            while ((satir = okuyucu.readLine()) != null) {
                // Satır içinde // işareti varsa yorum satırıdır
                if (satir.contains("//")) {
                    digerYorumSatırSayısı++;
                }

                // satır /* işareti ile başlıyorsa yorum bloğu içine gir
                if (!satir.trim().startsWith("/**") && (yorumBloğuIçinde || satir.trim().startsWith("/*"))) {
                    yorumBloğuIçinde = true;
                    
                    // Eğer yorum bloğuna geçiş satırında yorum işareti yoksa, bu satırı yorum satırı olarak kabul et
                    if (!satir.trim().startsWith("/*") && !satir.trim().endsWith("*/")) {
                        digerYorumSatırSayısı++;
                    }
                }

                // Yorum bloğu içindeyken ve satırın sonunda */ işareti varsa, yorum bloğu dışına çık
                if (yorumBloğuIçinde && satir.contains("*/")) {
                    yorumBloğuIçinde = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return digerYorumSatırSayısı;
    }
    
    
    //boşluklar ve yorum satırları hariç kod satırlarını hesaplayan fonksiyon
    private static int kodSatırlarınıSay(File dosya) {
    	  int kodSatırSayısı = 0;
    	    boolean yorumBloğu = false;

    	    try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
    	        String satır;
    	        while ((satır = okuyucu.readLine()) != null) {
    	            satır = satır.trim();

    	            // Yorum satırı kontrolü
    	            if (satır.startsWith("//")) {
    	                continue; // Yorum satırı, atla
    	            }

    	            // Yorum bloğu içindeyse
    	            if (yorumBloğu) {
    	                if (satır.contains("*/")) {
    	                    yorumBloğu = false; // Yorum bloğu sonu
    	                    satır = satır.substring(satır.indexOf("*/") + 2); // Yorum bloğunun sonrasını al
    	                    if (satır.isEmpty()) {
    	                        continue; // Yorum bloğu sonrası boşsa, atla
    	                    }
    	                } else {
    	                    continue; // Yorum bloğu içinde, atla
    	                }
    	            }

    	            if (satır.startsWith("/*")) {
    	                if (satır.endsWith("*/")) {
    	                    continue; // Yorum bloğu tek satır, atla
    	                } else {
    	                    yorumBloğu = true; // Yorum bloğu başlangıcı
    	                    continue; // Yorum bloğu içinde, atla
    	                }
    	            }

    	            // Yorum bloğu içinde değilse ve satır boş değilse, kod satırı olarak say
    	            if (!satır.isEmpty()) {
    	                kodSatırSayısı++;
    	            }
    	        }
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }

    	    return kodSatırSayısı;
    }

    // LOC (Line of Code) yani yorum satırları ve boşlukların da dahil olduğu, her şeyin dahil olduğu toplam satır sayısını hesaplayan fonksiyon
    private static int locSatırlarınıSay(File dosya) {
    	int locSayısı = 0;

        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
            String satır;
            while ((satır = okuyucu.readLine()) != null) {
                // Hiç bir şey ayırt etmeden her bir satırı doğrudan sayar
                locSayısı++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locSayısı;
    }

    // Fonksiyon sayısını hesaplayan fonksiyon
    private static int fonksiyonSayısınıSay(File dosya) {
        int fonksiyonSayısı = 0;
        boolean fonksiyonİçinde = false;

        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosya))) {
            String satır;
            while ((satır = okuyucu.readLine()) != null) {
                satır = satır.trim();
                //eğer bir satır "void", "int", "String", "public", "private", "protected" gibi fonksiyon anahtar kelimeleriyle başlıyorsa fonskiyon olabilir.
                
                if (satır.startsWith("void") || satır.startsWith("int") || satır.startsWith("String") || satır.startsWith("public") || satır.startsWith("private") || satır.startsWith("protected")) {
                    fonksiyonİçinde = true;
                }
                //aynı zamanda süslü parantezler de içermelidir
                if (fonksiyonİçinde && satır.contains("{")) {
                    fonksiyonSayısı++;
                }
                if (satır.contains("}")) {
                    fonksiyonİçinde = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        fonksiyonSayısı = fonksiyonSayısı - 1;
        
        if(fonksiyonSayısı <0 )
        	return 0;
        else
        return fonksiyonSayısı ;
        
        
	}
    
    //yorum sapma yüzdesini hesaplayan fonksiyon
    private static double yorumSapmaYuzdesiHesapla(int javadocSatirSayisi, int digerYorumSatirSayisi, int kodSatirSayisi, int fonksiyonSayisi) {
        // YG hesaplanması
        double YG = ((javadocSatirSayisi + digerYorumSatirSayisi) * 0.8) / fonksiyonSayisi;
        
        // YH hesaplanması
        double YH = ((double) kodSatirSayisi / fonksiyonSayisi) * 0.3;
        
        // Yorum Sapma Yüzdesinin Hesaplanması
        return ((100 * YG) / YH) - 100;
    }

}
