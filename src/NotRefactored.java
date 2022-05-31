
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Преобразовать файл так, чтобы выходной файл содержал только цифровые символы из исходного
 */
public class NotRefactored {
    // Писать логику в main методе не стоит хотя бы потому что single responsibility
    public static void main(String[] args) throws IOException {
        //нет смысла использовать StringBuffer если работаем в один поток,
        // для таких случаев есть StringBuilder т.к. он быстрей (в однопоточке)
        StringBuffer result = new StringBuffer();
        var path = Path.of("test.txt");

        BufferedReader r = null;
        //лучше использовать try with resources симпатичней и работает лучше
        try {
            //Так же многократно вложенные catch блоки не оч удобочитаемы скобочках можно
            // указать несколько видов исключений которые мы ожидаем через "|"
            try {
                r = new BufferedReader(new FileReader(path.toFile()));
            } catch (FileNotFoundException e) {
                // Пустой catch плохая практика
                // в данном случае если мы не нашли файл упадем с ошибкой ниже, толку от этого исключения ноль
            }

            String l;
            while ((l = r.readLine()) != null) {
                //если уж вызываем вложенный класс то нет смысла ссылаться на самого же себя (Main.)
                // плохой нейминг 1) метод не занимается чтением 2) два рядом стоящих вызова с одинаковым названием
                // и разной логикой не ломает мозг
                NotRefactored.readLine(result, l);
            }
        } catch (IOException e) {
            // тут наверно нужно смотреть на бизнес логику, но в таком виде особо смысла от кастомного исключения нет
            throw new WrongFileFormatException();
        } finally {
            // секция finally не нужна если использовать try with resources
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    //пустой catch
                }
            }
        }
        //стоит поместить в try т.к. тоже выкидывает наследника ioexception
        Files.writeString(Path.of("result.txt"), result.toString());
    }

    // полагаю причин делать его статическим, после выноса из мейн метода больше нет
    // не используемый возвращаемый тип private boolean \ return false;
    private static boolean readLine(StringBuffer accum, String s) {
        //Optional инициализируется пустым Optional, а не присвоением null
        //в текущей реализации он вообще не нужен
        Optional<Character> chr = null;
        for (int i = 0; i < s.length(); i++) {
            //new Character('0') - deprecated упаковка тут в не нужна
            //сама логика выборки числа не рабочая, Character.isDigit(c) отлично справится проверкой на "число"
            // в трех местах берем s.charAt(i) можно сэкономить если вынести этот вызов в переменную
            if ((s.charAt(i) > new Character('0')) || (s.charAt(i) < new Character('9'))) {
                //сюда нужно поместить вызов accum.append дабы собирать нужные нам символы иначе большая часть затрется
                chr = Optional.ofNullable(s.charAt(i));
            }
        }
        // в продолжение к инициализации такая проверка (chr != null) не сработает и мы выпадем с ошибкой
        // во время проверки chr.get()
        // переписывается в одну строчку да еще и работать будет chr.ifPresent(accum::append);
        if (chr != null) {
            accum.append(chr.get());
        }
        return false;
    }
    // Стоит вынести как внешний класс, а не использовать как внутренний
    // Статическим ему быть незачем
    // В целом не понятно вписывается ли это исключение в наш код
    public static class WrongFileFormatException extends RuntimeException {
        // вообще не испольщуем эту переменную
        public String code;
        // Стоило бы добавить гибкости и передавать в конструктор хотя бы сообщение ошибки
        public WrongFileFormatException() {
            //hard code
            super("File of a wrong format");
        }
    }
}
