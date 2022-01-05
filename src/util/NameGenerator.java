package util;

public class NameGenerator {
	private static String[] currentLang = Language.swedish;
	
	public static void setLanguage(String[] lang) {
		currentLang = lang;
	}
	
	public static String generateFullName() {
		return generateForename() + " " + generateSurname();
	}
	
	public static String generateForename() {
		int syllables = RandUtil.getInt(2, 3);
		String forename = "";
		for (int i = 0; i < syllables; i++) {
			forename += RandUtil.pickRandomFromArray(currentLang);
		}
		
		return forename;
	}
	
	public static String generateSurname() {
		int syllables = RandUtil.getInt(2, 4);
		String forename = "";
		for (int i = 0; i < syllables; i++) {
			forename += RandUtil.pickRandomFromArray(currentLang);
		}
		
		return forename;
	}
}
