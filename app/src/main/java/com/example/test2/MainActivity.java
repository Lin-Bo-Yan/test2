package com.example.test2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button open_pdf,create_pdf;
    String path;
    Context context;
    boolean isPermissions = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                isPermissions = true;
            }
        } else if (requestCode == FILE_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }

    }

    private void initView(){
        open_pdf = findViewById(R.id.open_pdf);
        create_pdf = findViewById(R.id.create_pdf);
        context = getApplicationContext();
        path = FileUtils.getAppPath() + "test.pdf";
        //pdf保存路徑
        create_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPDF(path);
            }
        });
        open_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPDF();
            }
        });
    }

    protected final int FILE_REQUEST = 42;
    private void openPDF(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StringUtils.HaoLog("ggg= "+path);
                FileUtils.openFile(context,new File(path));
            }
        },1000);
    }

    private void createPDF(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        file.getParentFile().mkdirs();
        //創建Document
        PdfWriter writer = null;

        try {
            writer = new PdfWriter(new FileOutputStream(path));
        }catch (FileNotFoundException e){
            StringUtils.HaoLog("ggg= "+e);
        }
        PdfDocument pdf_document = new PdfDocument(writer);
        //生成PDF文檔訊息
        PdfDocumentInfo info = pdf_document.getDocumentInfo();
        //標題
        info.setTitle("史萊姆的第一個家");
        //作者
        info.setAuthor("boyan");
        //創建日期
        info.setCreator("2023/6/21");
        //關鍵詞
        info.setKeywords("pdf");
        //科目
        info.setSubject("test");

        Document document = new Document(pdf_document, PageSize.A4,true);
        //文字字體
        PdfFont font = null;
        try {
            font = PdfFontFactory.createFont("STSuong-Light","UniGB-UCS2-H");
        }catch (IOException e){
            e.printStackTrace();
        }
        float title_size = 36.0f;
        float text_title_size = 30.0f;
        float text_size = 24.0f;
        Color title_color = new DeviceRgb(0,0,0);
        Color text_title_color = new DeviceRgb(65,136,160);
        Color rexr_color = new DeviceRgb(43,43,43);
        //分隔符
        // 實線：SolidLine()，點線：DottedLine()，儀表盤線：DashedLine()
        LineSeparator separator = new LineSeparator(new SolidLine());
        separator.setStrokeColor(new DeviceRgb(0,0,68));

        //添加大標題
        Text title = new Text("pdf文件標題").setFont(font).setFontSize(title_size).setFontColor(title_color);
        Paragraph paragraph_title = new Paragraph(title).setTextAlignment(TextAlignment.CENTER);
        document.add(paragraph_title);


        //添加頁面，頁腳，水印
        Rectangle pageSize;
        PdfCanvas canvas;
        int pages = pdf_document.getNumberOfPages();
        for(int i = 1 ; i <= pages; i++){
            PdfPage page = pdf_document.getPage(i);
            pageSize = page.getPageSize();
            canvas = new PdfCanvas(page);
            //頁眉
            canvas.beginText().setFontAndSize(font,7)
                    .moveText(pageSize.getWidth()/2-18,pageSize.getHeight()-10)
                    .showText("我是頁眉")
                    .endText();

            //頁腳
            canvas.setStrokeColor(text_title_color)
                    .setLineWidth(.2f)
                    .moveTo(pageSize.getWidth()/2-30,20)
                    .lineTo(pageSize.getWidth()/2-30,20).stroke();
            canvas.beginText().setFontAndSize(font,7)
                    .moveText(pageSize.getWidth()/2-6,10)
                    .showText(String.valueOf(i))
                    .endText();
            //水印
            Paragraph paragraph = new Paragraph("Quinto").setFontSize(60);
            canvas.saveState();
            PdfExtGState gs1 = new PdfExtGState().setFillOpacity(0.2f);
            canvas.setExtGState(gs1);
            document.showTextAligned(paragraph,pageSize.getWidth()/2,pageSize.getHeight()/2
            ,pdf_document.getPageNumber(page),TextAlignment.CENTER, VerticalAlignment.MIDDLE,45);
            canvas.restoreState();
        }

        document.close();
        StringUtils.HaoLog("PDF文件已生成");

    }

}