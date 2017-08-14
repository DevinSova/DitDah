package com.neoskeeter.ditdah.Utilities;

public class Translator {

    private final static char[] LETTERS = {'a' , 'b'   , 'c'   , 'd'  , 'e', 'f'   , 'g'  , 'h'   , 'i' , 'j'   , 'k'  , 'l'   , 'm' , 'n' , 'o'  , 'p'   , 'q'   , 'r'  , 's'  , 't', 'u'  , 'v'   , 'w'  , 'x'   , 'y'   , 'z'   , ' ', '\n'};
    private static String[] MORSE =       {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "|", "\n"};

    public static String stringToMorse(String s)
    {
        String translation = "";
        s = s.toLowerCase();
        for(int i = 0; i < s.length(); i++)
        {
            for(int j = 0; j < LETTERS.length; j++)
            {
                if(s.charAt(i) == LETTERS[j]) {
                    translation += MORSE[j];
                    if(i != s.length() - 1 )
                        translation += ' ';
                    break;
                }
            }
        }
        return translation;
    }

    public static String charToMorse(char c)
    {
        for(int i = 0; i < LETTERS.length; i++)
        {
            if(LETTERS[i] == c)
                return MORSE[i];
        }
        return "?";
    }
    /*
    public static String morseToString(String m)
    {
        //TODO: Finish me.
        return null;
    }
     */
}
