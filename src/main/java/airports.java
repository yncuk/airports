import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class airports {
    public static void main(String[] args) throws IOException {
        Path PATH = Paths.get("airports.csv");
        Map<String, List<Map<Integer, String>>> resultMap = new HashMap<>();

        BufferedReader csvReader = new BufferedReader(new FileReader("airports.csv"));
        String row;
        Integer index = 0;
        while ((row = csvReader.readLine()) != null) {
            index++;
            String[] data = row.split(",");
            Map<Integer, String> indexAirports = new HashMap<>();
            indexAirports.put(index, data[1]);
            if (resultMap.get(data[1].substring(1, 2)) != null) {
                resultMap.get(data[1].substring(1, 2)).add(indexAirports);
            } else {
                List<Map<Integer, String>> currentList = new ArrayList<>();
                currentList.add(indexAirports);
                resultMap.put(data[1].substring(1, 2), currentList);
            }
        }
        csvReader.close();

        while (true) {
            System.out.println("Введите фильтр");
            Scanner scanner = new Scanner(System.in);
            String filter = scanner.nextLine();
            if (filter.equals("!quit")) {
                System.exit(0);
            }

            System.out.println("Введите начало названия аэропорта");
            scanner = new Scanner(System.in);
            String nameAirports = scanner.nextLine();
            if (nameAirports.equals("!quit")) {
                System.exit(0);
            }

            long startTime = System.nanoTime();
            List<Map<Integer, String>> list = resultMap.get(nameAirports.substring(0, 1));
            List<Integer> listOfLineNumber = new ArrayList<>();

            for (Map<Integer, String> map : list) {
                for (String str : map.values()) {
                    if (str.startsWith('"' + nameAirports)) {
                        Set<Integer> resultIndex = map.keySet();
                        listOfLineNumber.addAll(resultIndex);
                    }
                }
            }
            List<String> extractedLineRes = new ArrayList<>();
            if (filter.isBlank()) {
                for (Integer number : listOfLineNumber) {
                    String extractedLine = Files.readAllLines(PATH).get(number - 1);
                    extractedLineRes.add(extractedLine);
                    System.out.println(extractedLine);
                }
            } else {

                for (Integer number : listOfLineNumber) {
                    String extractedLine = Files.readAllLines(PATH).get(number - 1);
                    String[] data2 = extractedLine.split(",");
                    String[] data3 = filter.split("column\\[");
                    List<Integer> filterColumn = new ArrayList<>();
                    List<String> operator = new ArrayList<>();
                    List<String> variable = new ArrayList<>();
                    for (int i = 1; i < data3.length; i++) {
                        filterColumn.add(Integer.valueOf(data3[i].substring(0, 1)));
                        operator.add(data3[i].substring(2, 3));
                        String[] var = data3[i].split("<>|>|<|=|&");
                        variable.add(var[1].substring(0, var[1].length() - 1));
                    }
                    for (int i = 0; i < filterColumn.size(); i++) {
                        switch (operator.get(i)) {
                            case "=":
                                if (variable.get(0).charAt(0) != '\'') {
                                    if (Objects.equals(data2[filterColumn.get(i) - 1], variable.get(0))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                } else {
                                    if (Objects.equals(data2[filterColumn.get(i) - 1], variable.get(0).substring(1))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                }

                                break;
                            case ">":
                                if (filterColumn.get(i) == 1) {
                                    if (Integer.parseInt(data2[filterColumn.get(i) - 1]) > Integer.parseInt(variable.get(0))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                } else if (filterColumn.get(i) == 7 || filterColumn.get(i) == 8) {
                                    if (Double.parseDouble(data2[filterColumn.get(i) - 1]) > Double.parseDouble(variable.get(0).substring(1))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                }
                                break;
                            case "<":
                                if (filterColumn.get(i) == 1) {
                                    if (Integer.parseInt(data2[filterColumn.get(i) - 1]) < Integer.parseInt(variable.get(0))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                } else if (filterColumn.get(i) == 7 || filterColumn.get(i) == 8) {
                                    if (Double.parseDouble(data2[filterColumn.get(i) - 1]) < Double.parseDouble(variable.get(0).substring(1))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                }
                                break;
                            default:
                                if (variable.get(0).charAt(0) != '\'') {
                                    if (!Objects.equals(data2[filterColumn.get(i) - 1], variable.get(0))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                } else {
                                    if (!Objects.equals(data2[filterColumn.get(i) - 1], variable.get(0).substring(1))) {
                                        extractedLineRes.add(extractedLine);
                                        System.out.println(data2[1] + '[' + extractedLine + ']');
                                    }
                                }
                                break;
                        }
                    }
                }
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            System.out.println("Количество найденных строк: " + extractedLineRes.size() + " Время, затраченное на поиск: " + duration + " мс");
        }
    }
}
