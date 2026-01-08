package com.example.myoffice.ui.services.classes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ReportGenerator
{
    public static void GeneratePersonalStatement(Context context, StatementClass statement) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return; // Завершите выполнение, если разрешение не предоставлено
        }

        // Создание нового документа
        XWPFDocument document = new XWPFDocument();

        try {
            // Параграф для "Начальнику цеха"
            XWPFParagraph headerParagraph = document.createParagraph();
            headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setFontSize(14);
            headerRun.setFontFamily("Times New Roman");
            headerRun.setText("Директору завода");
            headerRun.addBreak();
            headerRun.setText("ОАО 'ГЗЛиН'"); // Должность
            headerRun.addBreak();
            headerRun.setText("Иванову Ивану Ивановичу"); // ФИО
            headerRun.addBreak();

            // Параграф для "ЗАЯВЛЕНИЕ"
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setFontSize(14);
            titleRun.setFontFamily("Times New Roman");
            titleRun.setText("ЗАЯВЛЕНИЕ");
            titleRun.setBold(true);
            titleRun.addBreak();

            // Параграф для основного текста
            XWPFParagraph bodyParagraph = document.createParagraph();
            bodyParagraph.setAlignment(ParagraphAlignment.BOTH);

            XWPFRun bodyRun = bodyParagraph.createRun();
            bodyRun.setFontFamily("Times New Roman");
            bodyRun.setFontSize(14);

            String date_string = "с " + statement.getStartDate().toString() + " по " + statement.getEndDate().toString();
            String statement_description = " без сохранения заработной платы ";

            if(statement.getStartDate() == statement.getEndDate())
                date_string = "на " + statement.getStartDate().toString();

            if(statement.getStatementIntType() == StatementClass.E_STATEMENT_TYPE.STATEMENT_TYPE_VACATION.ordinal())
                statement_description = " ";

            bodyRun.setText(String.format("Прошу предоставить %s%s%s", statement.getStatementName().toLowerCase(), statement_description, date_string));
            bodyRun.addBreak();
            bodyRun.addBreak();

            XWPFParagraph footerParagraph = document.createParagraph();
            footerParagraph.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun footerRun = footerParagraph.createRun();
            footerRun.setFontFamily("Times New Roman");
            footerRun.setFontSize(14);
            Log.d("DEBUG",  String.format("getfullname = %s", statement.getFullName()));
            footerRun.setText(statement.getFullName()); // ФИО сотрудника
            footerRun.addBreak();
            footerRun.setText(statement.getCreateDate().toString());
            footerRun.addBreak();

            // Сохранение документа в внутреннем хранилище
            String folderName = "Statements";
            String filePath = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/" + folderName;

            // Создаем директорию, если она не существует
            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = String.format(Locale.US, "Заявление№%d.docx", statement.getStatementID());
            File file = new File(directory, fileName);

            try (FileOutputStream out = new FileOutputStream(file)) {
                document.write(out);
                Toast.makeText(context, "Документ успешно сохранен: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка при создании документа: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}