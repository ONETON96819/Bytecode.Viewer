package the.bytecode.club.bytecodeviewer.translation;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.map.LinkedMap;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.BCV;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.resources.Resource;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * All of the supported languages
 *
 * TODO: Hindi, Bengali, Korean, Thai & Javanese need fonts to be supplied for them to show.
 *  The default font should be saved so it can be restored for latin-character based languages
 *
 * @author Konloch
 * @since 6/28/2021
 */
public enum Language
{
	ENGLISH("/translations/english.json", "English", "English", "en"),
	ARABIC("/translations/arabic.json", "????????", "English", "ar"),
	CROATIAN("/translations/croatian.json", "hrvatski", "English", "hr"),
	CZECH("/translations/czech.json", "??e??tina", "English", "cs"),
	BULGARIAN("/translations/bulgarian.json", "??????????????????", "English", "bg"),
	DANISH("/translations/danish.json", "dansk", "English", "da"),
	ESTONIAN("/translations/estonian.json", "Eesti", "English", "et"),
	FARSI("/translations/farsi.json", "?????????? ", "English", "fa"),
	FINNISH("/translations/finnish.json", "Suomen Kieli", "English", "fi"),
	FRENCH("/translations/french.json", "Fran??ais", "English", "fr"),
	GERMAN("/translations/german.json", "Deutsch", "German", "de"),
	GEORGIAN("/translations/georgian.json", "????????????????????? ?????????", "English", "ka"),
	GREEK("/translations/greek.json", "????????????????", "English", "el"),
	HAUSA("/translations/hausa.json", "Hausa", "English", "ha"),
	HEBREW("/translations/hebrew.json", "????????????????\u200E", "English", "iw", "he"),
	//HINDI("/translations/hindi.json", "???????????????", "English", "hi"),
	//BENGALI("/translations/bengali.json", "???????????????", "English", "bn"),
	HUNGARIAN("/translations/hungarian.json", "Magyar Nyelv", "English", "hu"),
	INDONESIAN("/translations/indonesian.json", "bahasa Indonesia", "English", "id"),
	ITALIAN("/translations/italian.json", "Italiano", "English", "it"),
	JAPANESE("/translations/japanese.json", "?????????", "English", "ja"),
	LATIVAN("/translations/lativan.json", "Lativan", "English", "lv"),
	LITHUANIAN("/translations/lithuanian.json", "Lietuvi??", "English", "lt"),
	//JAVANESE("/translations/javanese.json", "????????????", "English", "jw", "jv"),
	//KOREAN("/translations/korean.json", "Korean", "English", "ko"),
	MALAY("/translations/malay.json", "Bahasa Melayu", "English", "ms"),
	MANDARIN("/translations/mandarin.json", "?????????", "Mandarin", "zh-CN", "zh_cn", "zh"),
	NEDERLANDS("/translations/nederlands.json", "Nederlands", "English", "nl"), //dutch
	NORWEGIAN("/translations/norwegian.json", "Norsk", "English", "no"),
	POLISH("/translations/polish.json", "Polski", "English", "pl"),
	PORTUGUESE("/translations/portuguese.json", "Portugu??s", "English", "pt"),
	ROMANIAN("/translations/romanian.json", "Rom??n??", "English", "ro"),
	RUSSIAN("/translations/russian.json", "??????????????", "English", "ru"),
	SLOVAK("/translations/slovak.json", "Slovensky", "English", "sk"),
	SLOVENIAN("/translations/slovenian.json", "Sloven????ina", "English", "sl"),
	SPANISH("/translations/spanish.json", "Espa??ol", "English", "es"),
	SERBIAN("/translations/serbian.json", "???????????? ??????????", "English", "sr"),
	SWAHILI("/translations/swahili.json", "Kiswahili", "English", "sw"),
	SWEDISH("/translations/swedish.json", "svenska", "English", "sv"),
	//THAI("/translations/thai.json", "?????????????????????", "English", "th"),
	TURKISH("/translations/turkish.json", "T??rk??e", "English", "tr"),
	UKRAINIAN("/translations/ukrainian.json", "?????????????????????? ??????????", "English", "uk"),
	VIETNAMESE("/translations/vietnamese.json", "Ti???ng Vi???t", "English", "vi"),
	;
	
	private static final Map<String, Language> languageCodeLookup;
	
	static
	{
		languageCodeLookup = new LinkedHashMap<>();
		for(Language l : values())
			l.languageCode.forEach((langCode)->
					languageCodeLookup.put(langCode, l));
	}
	
	private final String resourcePath;
	private final String readableName;
	private final String htmlIdentifier;
	private final Set<String> languageCode;
	private Map<String, String> translationMap;
	
	Language(String resourcePath, String readableName, String htmlIdentifier, String... languageCodes)
	{
		this.resourcePath = resourcePath;
		this.readableName = readableName;
		this.htmlIdentifier = htmlIdentifier.toLowerCase();
		this.languageCode = new LinkedHashSet<>(Arrays.asList(languageCodes));
	}
	
	public void setLanguageTranslations() throws IOException
	{
		printMissingLanguageKeys();
		
		Map<String, String> translationMap = getTranslation();
		
		for(TranslatedComponents translatedComponents : TranslatedComponents.values())
		{
			TranslatedComponentReference text = translatedComponents.getTranslatedComponentReference();
			
			//skip translating if the language config is missing the translation key
			if(!translationMap.containsKey(text.key))
			{
				BCV.logE(true, resourcePath + " -> " + text.key + " - Missing Translation Key");
				continue;
			}
			
			//update translation text value
			text.value = translationMap.get(text.key);
			
			//translate constant strings
			try {
				TranslatedStrings str = TranslatedStrings.valueOf(text.key);
				str.setText(text.value);
			} catch (IllegalArgumentException ignored) { }
			
			//check if translation key has been assigned to a component,
			//on fail print an error alerting the devs
			if(translatedComponents.getTranslatedComponentReference().runOnUpdate.isEmpty())
					//&& TranslatedStrings.nameSet.contains(translation.name()))
			{
				BCV.logE(true, "TranslatedComponents:" + translatedComponents.name() + " is missing component attachment, skipping...");
				continue;
			}
			
			//trigger translation event
			translatedComponents.getTranslatedComponentReference().translate();
		}
	}
	
	public Map<String, String> getTranslation() throws IOException
	{
		if(translationMap == null)
		{
			translationMap = BytecodeViewer.gson.fromJson(
					Resource.loadResourceAsString(resourcePath),
					new TypeToken<HashMap<String, String>>() {}.getType());
		}
		
		return translationMap;
	}
	
	//TODO
	// When adding new Translation Components:
	// 1) start by adding the strings into the english.json
	// 2) run this function to get the keys and add them into the Translation.java enum
	// 3) replace the swing component (MainViewerGUI) with a translated component
	//    and reference the correct translation key
	// 4) add the translation key to the rest of the translation files
	public void printMissingLanguageKeys() throws IOException
	{
		if(this != ENGLISH)
			return;
		
		LinkedMap<String, String> translationMap = BytecodeViewer.gson.fromJson(
				Resource.loadResourceAsString(resourcePath),
				new TypeToken<LinkedMap<String, String>>(){}.getType());
		
		Set<String> existingKeys = new HashSet<>();
		for(TranslatedComponents t : TranslatedComponents.values())
			existingKeys.add(t.name());
		
		for(String key : translationMap.keySet())
			if(!existingKeys.contains(key))
				BCV.logE(true, key + ",");
	}
	
	public String getResourcePath()
	{
		return resourcePath;
	}
	
	public Set<String> getLanguageCode()
	{
		return languageCode;
	}
	
	public String getReadableName()
	{
		return readableName;
	}
	
	public String getHTMLPath(String identifier)
	{
		return "translations/html/" + identifier + "." + htmlIdentifier +  ".html";
	}
	
	public static Map<String, Language> getLanguageCodeLookup()
	{
		return languageCodeLookup;
	}
}
