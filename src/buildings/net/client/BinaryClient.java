/**
 * Реализуйте клиентскую часть приложения в новом классе buildings.net.client.BinaryClient,
 * содержащем метод main().
 * Входными параметрами программы (аргументами командной строки) являются имена трех файлов.
 * Первый файл существует на момент запуска программы и содержит в текстовом виде информацию
 * о зданиях (например, одна строка – одно здание). Второй файл существует на момент
 * запуска программы и содержит в текстовом виде информацию о видах зданий (например, одна строка –
 * одно слово, определяющее вид здания). Количество записей в первом и втором файле можно считать
 * соответствующим друг другу, но неизвестным заранее (т.е. оно даже не записано в первой строке
 * файлов). Файлы можно считать корректными. Третий файл должен быть создан программой в ходе работы
 * и должен хранить в текстовом виде оценки стоимости зданий (например, одна строчка – одна оценка
 * стоимости).
 * Программа должна установить через сокеты соединение с сервером, после чего считывать из первого
 * и второго файла данные о здании, передавать их в бинарной форме серверу и получать от него
 * результат оценки стоимости здания, и так по очереди для всех исходных данных.
 * Для обеспечения работы приложения потребуется создание протокола взаимодействия клиентской и
 * серверной частей: порядка передачи данных, определения условия завершения передачи данных.
 * Считывание данных из первого файла, а также запись данных в поток сокета рекомендуется
 * реализовать с помощью средств класса Buildings. Также рекомендуется реализовать вывод информации
 * о текущей операции в консоль (например, с помощью ранее реализованных методов toString() зданий).
 */
package buildings.net.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import buildings.Buildings;
import buildings.factory.DwellingFactory;
import buildings.factory.HotelFactory;
import buildings.factory.OfficeFactory;
import buildings.interfaces.Building;

public class BinaryClient {

    public void test() throws FileNotFoundException {
        File file = new File("");
        Reader in = new InputStreamReader(new FileInputStream(file));
        try {
            in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File buildingType = new File(args[0]);
        Scanner type = new Scanner(buildingType);

        File buildingInfo = new File(args[1]);
        Reader in = new FileReader(buildingInfo);

        File buildingCosts = new File(args[2]);
        FileOutputStream fos = new FileOutputStream(buildingCosts);
        PrintStream writeCostInFile = new PrintStream(fos);

        Socket socket = new Socket();
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        System.out.println("Client connected to socket.");
        System.out.println("Client writing channel & reading channel initialized.");


        while (type.hasNext() && !socket.isOutputShutdown()) {
            System.out.println("Client start reading info about some building...");
            String t = new String(type.next());
            dos.writeBytes(t);
            switch (t) {
                case "Hotel":
                    Buildings.setBuildingFactory(new HotelFactory());
                case "OfficeBuilding":
                    Buildings.setBuildingFactory(new OfficeFactory());
                case "Dwelling":
                    Buildings.setBuildingFactory(new DwellingFactory());
            }
            Building building = Buildings.readBuilding(in);
            Buildings.outputBuilding(building, dos);
            dos.flush();
            System.out.println("Client sent message to server.");
            Thread.sleep(1000);
            writeCostInFile.println((dis.read()));
            System.out.println("Client read message from server and wrote it in the file.");
        }

        writeCostInFile.close();
        dis.close();
        dos.close();
        in.close();
        System.out.println("Closing connections & channels on clentSide - DONE.");
    }
}
