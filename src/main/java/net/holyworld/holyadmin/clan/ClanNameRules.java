package net.holyworld.holyadmin.clan;

import java.util.Locale;

public final class ClanNameRules {
	public static final ClanNameRules INSTANCE = new ClanNameRules();

	private static final String[] EXCEPTIONS = {
		"ссср", "пипидастр", "фрик", "узбеки", "евреи", "казаки", "ансофт", "коммунисты",
		"цыгани", "пей_горно", "альтушки", "пикми", "партизаны", "сатурн", "arbuz", "балбесы"
	};

	private static final String[] CHEATS = {
		"чат", "чит", "cheat", "хак", "hack", "аим", "aim", "аимбот", "aimbot", "aura",
		"кайра", "killaura", "трай", "try", "нукер", "nuker", "бхоп", "bhop", "спидхак",
		"speedhack", "флай", "flight", "иксрей", "xray", "сурка", "автоклик", "autoclick",
		"макрос", "macro", "скрипт", "script", "клиент", "client", "эсп", "esp", "wallhack",
		"валлхак", "skid", "rapid", "clicker", "софт", "software", "freecam",
		"фрикам", "tracers", "hitbox", "хитбокс", "дюп", "dupe", "глитч", "glitch",
		"эксплоит", "exploit", "накрут", "обход", "вкло", "fdp", "метро", "metro"
	};

	private static final String[] PROFANITY = {
		"хуй", "хуе", "хую", "хуя", "пизд", "ебал", "ебат", "ебан", "ёбан", "ебаш",
		"бля", "бляд", "сука", "суки", "сучк", "пидор", "педик", "пидр", "гей", "геи",
		"гомо", "лох", "лохи", "мудак", "мудач", "долбо", "долбоёб", "долбоеб", "дебил",
		"даун", "дауны", "имбицил", "кретин", "тупые", "тупой", "шлюх", "проститутк",
		"гандон", "чмо", "ублюд", "мразь", "тварь", "козёл", "козел", "негр", "черномазы",
		"охуе", "нахуй", "похуй", "хуйн", "обоссан", "уёбищ", "уебищ"
	};

	private static final String[] GEO = {
		"москва", "петербург", "питер", "новосибирск", "екатеринбург", "казань",
		"нижний новгород", "самара", "омск", "челябинск", "ростов", "уфа", "краснодар",
		"воронеж", "пермь", "волгоград", "тольятти", "киев", "харьков", "одесса", "львов",
		"днепр", "минск", "гомель", "брест", "астана", "алматы", "ташкент", "баку",
		"ереван", "тбилиси", "кишинёв", "берлин", "париж", "лондон", "мадрид", "рим",
		"варшава", "прага", "вильнюс", "рига", "таллин", "хельсинки", "стокгольм",
		"нью-йорк", "нью йорк", "вашингтон", "пекин", "токио", "дубай", "анкара", "стамбул",
		"россия", "украина", "беларусь", "белоруссия", "казахстан", "узбекистан",
		"германия", "сша", "америка", "китай", "франция", "италия", "испания", "польша",
		"грузия", "армения", "азербайджан", "молдова", "молдавия", "чехия", "япония",
		"индия", "турция", "египет", "греция",
		"путин", "зеленский", "зеленски", "байден", "трамп", "лукашенко", "сталин",
		"гитлер", "мао", "медведев", "навальный", "шольц", "макрон"
	};

	private static final String[] ADULT = {
		"порно", "porn", "секс", "sex", "хентай", "hentai", "онлифанс", "onlyfans",
		"xvideos", "pornhub", "голые", "голая", "голый", "nude", "эрот", "сиськи",
		"сиски", "сосат", "минет", "анальн", "раком", "18+", "adult", "nsfw", "взросл"
	};

	private ClanNameRules() {
	}

	public boolean isForbidden(String clanName) {
		if (clanName == null) {
			return false;
		}
		String n = clanName.trim();
		if (n.isEmpty()) {
			return false;
		}
		String low = n.toLowerCase(Locale.ROOT);
		for (String ex : EXCEPTIONS) {
			if (low.equals(ex)) {
				return false;
			}
		}
		return containsAny(low, CHEATS)
			|| containsAny(low, PROFANITY)
			|| containsAny(low, GEO)
			|| containsAny(low, ADULT);
	}

	private static boolean containsAny(String s, String[] words) {
		for (String w : words) {
			if (s.contains(w)) {
				return true;
			}
		}
		return false;
	}
}
