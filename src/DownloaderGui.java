import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class DownloaderGui extends JFrame{
    private JPanel main;
    private JTextField drive_path;
    private JTextField file_path;
    private JTextField class_name;
    private JTextField loop_times;
    private JTextField load_times;
    private JTextField download_file_name;
    private JButton downloadBtn;

    public void init(){
        this.class_name.setText(".ndfHFb-c4YZDc-cYSp0e-Oz6c3e.ndfHFb-c4YZDc-cYSp0e-DARUcf-gSKZZ.ndfHFb-c4YZDc-neVct-RCfa3e");
        this.load_times.setText("50");
    }

    public DownloaderGui() {
        this.init();
        this.setContentPane(this.main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400,500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.downloadBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String chrome_drive_path = drive_path.getText();

                    if(chrome_drive_path.equals("") || Files.notExists(Paths.get(chrome_drive_path))){
                        throw new Exception("Chrome không thể rỗng hoặc không tồn tại file");
                    }

                    String file_url = file_path.getText();
                    if(file_url.equals("")){
                        throw new Exception("File cần tải không thể rỗng");
                    }

                    String className = class_name.getText();
                    if(className.equals("")){
                        throw new Exception("Classname không thể rỗng");
                    }

                    int loop_time = 0;
                    int load_time = 0;

                    try{
                        loop_time = Integer.valueOf(loop_times.getText());
                        load_time = Integer.valueOf(load_times.getText());

                        if(loop_time <= 0 || load_time <= 0){
                            throw new Exception();
                        }
                    }catch(Exception e2){
                        throw new Exception("Loop Time và Load Time phải là số nguyên > 0");
                    }
                    String dowload_file_name = download_file_name.getText();

                    if(dowload_file_name.equals("")){
                        throw new Exception("Download File Name không thể rỗng");
                    }

                    runDownloader(chrome_drive_path,file_url,className,loop_time,load_time,dowload_file_name);

                }catch (Exception ex){
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null,ex.getMessage());
                }

            }
        });
    }

    private void runDownloader(String chromeDrivePath, String url, String className, int loopTime, int loadTime, String dowloadFileName) {
        WebDriver webDriver = null;
        try {
            System.setProperty("webdriver.chrome.driver", chromeDrivePath);
            //String url = "https://drive.google.com/file/d/102tZC435LmbTCqAsggT-WatKERcC7qwz/view?usp=sharing";
            //String className = ".ndfHFb-c4YZDc-cYSp0e-Oz6c3e.ndfHFb-c4YZDc-cYSp0e-DARUcf-gSKZZ.ndfHFb-c4YZDc-neVct-RCfa3e";

            webDriver = new ChromeDriver();

            webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

            webDriver.navigate().to(url);

            WebElement element =  webDriver.findElements(By.cssSelector(className)).get(0);

            for(int i = 0;i < loopTime;i++){
                element.sendKeys(Keys.PAGE_DOWN);
                Thread.sleep(loadTime);
            }


            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            String script = "let jspdf = document.createElement( \"script\" );\n" +
                    "jspdf.onload = function () {\n" +
                    "let pdf = new jsPDF();\n" +
                    "let elements = document.getElementsByTagName( \"img\" );\n" +
                    "for ( let i in elements) {\n" +
                    "let img = elements[i];\n" +
                    "if (!/^blob:/.test(img.src)) {\n" +
                    "continue ;\n" +
                    "}\n" +
                    "let canvasElement = document.createElement( 'canvas' );\n" +
                    "let con = canvasElement.getContext( \"2d\" );\n" +
                    "canvasElement.width = img.width;\n" +
                    "canvasElement.height = img.height;\n" +
                    "con.drawImage(img, 0, 0,img.width, img.height);\n" +
                    "let imgData = canvasElement.toDataURL( \"image/jpeg\" , 1.0);\n" +
                    "pdf.addImage(imgData, 'JPEG' , 0, 0);\n" +
                    "pdf.addPage();\n" +
                    "}\n" +
                    "pdf.save( \""+dowloadFileName+".pdf\" );\n" +
                    "};\n" +
                    "jspdf.src = 'https://cdnjs.cloudflare.com/ajax/libs/jspdf/1.3.2/jspdf.min.js' ;\n" +
                    "document.body.appendChild(jspdf);";
            js.executeScript(script);

        }catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null,e.getMessage());
            webDriver.quit();
        }
    }


    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DownloaderGui().setVisible(true);
            }
        });
    }
}
