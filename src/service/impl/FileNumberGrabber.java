package service.impl;

import service.NumberGrabber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileNumberGrabber implements NumberGrabber {
    @Override
    public void grabNumbers(String inputFileName, String outputFileName) throws IOException {
        //исключение тут не пробрасываю т.к. кто его знает что в таком случае с ним захочет делать клиент,
        // вот он пусть и возится =))
        // можно конечно добавить .parallel(),
        // но я бы сначала посмотрел какого объема файлы, не всегда это будет быстрей
        List<Character> digits = Files.lines(Paths.get(inputFileName))
                .flatMapToInt(String::chars)
                .mapToObj(ch -> (char) ch)
                .filter(Character::isDigit)
                .collect(Collectors.toList());
        Files.writeString(Paths.get(outputFileName), digits.toString());
    }

}
