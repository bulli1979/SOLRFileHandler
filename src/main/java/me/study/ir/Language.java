package me.study.ir;

public enum Language {
	DE("german"),FR("french"),IT("italian"),EN("english"),ESP("spanish");
	public String value;
	
	private Language(String value) {
		this.value = value;
	}

	public static Language findLanguage(String language) {
		for(Language l : Language.values()) {
			if(l.value.equals(language)) {
				return l;
			}
		}
		return null;
	}
}
