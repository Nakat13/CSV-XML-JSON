import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class Main {
    public static String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
    public static String fileName = "data.csv";
    public static String jsonFile = "data.json";
    public static String jsonFile2 = "data2.json";
    public static String xmlFile = "data.xml";
    private static List<Employee> employees = new ArrayList<>();

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            staff.forEach(System.out::println);
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> String listToJson(List list) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String jsonFile) {
        try (FileWriter file = new FileWriter(jsonFile)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlFile));

        NodeList employeeElements = document.getDocumentElement().getElementsByTagName("employee");
        for (int i = 0; i < employeeElements.getLength(); i++) {
            Node employee = employeeElements.item(i);
            if (Node.ELEMENT_NODE == employee.getNodeType()) {
                Element element = (Element) employee;
                long id = Long.parseLong(element.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue());
                String firstName = element.getElementsByTagName("firstName").item(0).getChildNodes().item(0).getNodeValue();
                String lastName = element.getElementsByTagName("lastName").item(0).getChildNodes().item(0).getNodeValue();
                String country = element.getElementsByTagName("country").item(0).getChildNodes().item(0).getNodeValue();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getChildNodes().item(0).getNodeValue());
                employees.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        String json2 = listToJson(employees);
        System.out.println(json2);
        writeString(json2, jsonFile2);
        return null;
    }


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, NullPointerException {

        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);

        writeString(json, jsonFile);

        List<Employee> list2 = parseXML(xmlFile);

    }

}

