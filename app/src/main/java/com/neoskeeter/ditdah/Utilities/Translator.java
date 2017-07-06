package com.neoskeeter.ditdah.Utilities;

public class Translator {

    private final static char[] letters = {'a' , 'b'   , 'c'   , 'd'  , 'e', 'f'   , 'g'  , 'h'   , 'i' , 'j'   , 'k'  , 'l'   , 'm' , 'n' , 'o'  , 'p'   , 'q'   , 'r'  , 's'  , 't', 'u'  , 'v'   , 'w'  , 'x'   , 'y'   , 'z', ' '};
    private static String[] morse = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "|"};

    public static String stringToMorse(String s)
    {
        String translation = "";
        s = s.toLowerCase();
        for(int i = 0; i < s.length(); i++)
        {
            for(int j = 0; j < letters.length; j++)
            {
                if(s.charAt(i) == letters[j]) {
                    translation += morse[j];
                    if(i != s.length() - 1 )
                        translation += ' ';
                    break;
                }
            }

        }
        return translation;
    }
    /*
    public static String morseToString(String m)
    {
        //TODO: Finish me.
        return null;
    }
     */
}
