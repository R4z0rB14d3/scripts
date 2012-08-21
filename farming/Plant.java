package scripts.farming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.powerbot.game.api.util.Filter;

public class Plant {
	public Plant(String name_, int id_, int t_, int low_, int mid_,
			String seeded, String grown, String diseased, String dead) {
		this(name_, id_, t_, low_, mid_, mid_,seeded,grown,diseased,dead);
	}

	public Plant(String name_, int id_, int t_, int low_, int mid_, int high_,
			String seeded_, String grown_, String diseased_, String dead_) {
		name = name_;
		id = id_;
		t = t_;
		low = low_;
		mid = mid_;
		high = high_;
		seeded = seeded_;
		grown = grown_;
		diseased = diseased_;
		dead = dead_;
	}

	String name;
	String seeded,grown,diseased,dead;
	
	public String getSeededName() {
		return seeded;
	}
	public String getGrownName() {
		return grown;
	}
	public String getDiseasedName() {
		return diseased;
	}
	public String getDeadName() {
		return dead;
	}

	public String toString() {
		return name;
	}

	public static final int Marigold = 5096;
	public static final int Rosemary = 5097;
	public static final int Nasturtium = 5098;
	public static final int WoadLeaf = 5099;
	public static final int Limpwurt = 5100;
	public static final int WhiteLily = 14589;

	public static final int Potato = 5318;
	public static final int Onion = 5319;
	public static final int Cabbage = 5324;
	public static final int Tomato = 5322;
	public static final int Sweetcorn = 5320;
	public static final int Strawberry = 5323;
	public static final int Watermelon = 5321;

	public static final int Guam = 5291;
	public static final int Marrentil = 5292;
	public static final int Tarromin = 5293;
	public static final int Harralander = 5294;
	public static final int Ranarr = 5295;
	public static final int SpiritWeed = 12176;
	public static final int Toadflax = 5296;
	public static final int Irit = 5297;
	public static final int Wergali = 14870;
	public static final int Avantoe = 5298;
	public static final int Kwuarm = 5299;
	public static final int Snapdragon = 5300;
	public static final int Cadantine = 5301;
	public static final int Lantadyme = 5302;
	public static final int DwarfWeed = 5303;
	public static final int Torstol = 5304;
	public static final int Fellstalk = 21621;
	public static final int GoutTuber = 6311;

	int low, mid, high;
	int id;
	int t;

	public boolean compost = false;
	public List<ProtectionItem> protectionItems = new ArrayList<ProtectionItem>();

	public class ProtectionItem {
		public int id;
		public int idNoted;
		public int amount;
	}

	public Plant addProtectionItem(int id, int amount) {
		return addProtectionItem(id, id + 1, amount);
	}

	public Plant addProtectionItem(int id, int idNoted, int amount) {
		ProtectionItem pitem = new ProtectionItem();
		pitem.id = id;
		pitem.idNoted = idNoted;
		pitem.amount = amount;
		protectionItems.add(pitem);
		return this;
	}

	public List<Product> products = new ArrayList<Product>();

	public Plant add(ProductType type, int id) {
		products.add(new Product(type, id));
		return this;
	}

	public int getLow() {
		return low;
	}

	public int getMid() {
		return mid;
	}

	public int getHigh() {
		return high;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return t;
	}

	public static List<Plant> getSeeds(Filter<Plant> filter) {
		List<Plant> filtered = new ArrayList<Plant>();
		for (Entry<Integer, Plant> seed : seeds.entrySet()) {
			if (filter.accept(seed.getValue()))
				filtered.add(seed.getValue());
		}
		return filtered;
	}

	private static HashMap<Integer, Plant> loadSeeds() {
		HashMap<Integer, Plant> seeds = new HashMap<Integer, Plant>();

		Plant[] seedsPlain = {
				new Plant("Marigold", Plant.Marigold, Patches.Flower, 0x08,
						0x0c,"Marigold","Marigold","Diseased marigold","Dead marigold").add(ProductType.FLOWER, 6010),
				new Plant("Rosemary", Plant.Rosemary, Patches.Flower, 0x0d,
						0x11,"Rosemary","Rosemary","Diseased rosemary","Dead rosemary").add(ProductType.FLOWER, 6014),
				new Plant("Nasturtium", Plant.Nasturtium, Patches.Flower, 0x12,
						0x16,"Nasturtium","Nastiurtium","Diseased nasturtium","Dead nasturtium").add(ProductType.FLOWER, 6012),
				new Plant("Woad", Plant.WoadLeaf, Patches.Flower, 0x17, 0x1b,"Herbs","Potato","Diseased potato","Dead potato")
						.add(ProductType.FLOWER, 1793),
				new Plant("Limpwurt plant", Plant.Limpwurt, Patches.Flower,
						0x1c, 0x20,"Limpwurt plant","Limpwurt plant","Diseased limpwurt plant","Dead limpwurt plant").add(ProductType.FLOWER, 225),
				new Plant("White lily", Plant.WhiteLily, Patches.Flower, 0x25,
						0x29,"White lily","White lily","Diseased potato","Dead potato").add(ProductType.FLOWER, 14583),
				new Plant("Potato", Plant.Potato, Patches.Allotment, 0x06,
						0x0a, 0x0c,"Herbs","Potato","Diseased potato","Dead potato").addProtectionItem(6032, 2).add(
						ProductType.VEGETABLE, 1942),
				new Plant("Onion", Plant.Onion, Patches.Allotment, 0x0d, 0x11,
						0x13,"Onion","Onion","Diseased onion","Dead onion").addProtectionItem(5438, 1).add(
						ProductType.VEGETABLE, 1957),
				new Plant("Cabbage", Plant.Cabbage, Patches.Allotment, 0x14,
						0x18, 0x1a,"Cabbages","Cabbages","Diseased cabbages","Dead cabbages").addProtectionItem(5458, 1).add(
						ProductType.VEGETABLE, 1965),
				new Plant("Tomato", Plant.Tomato, Patches.Allotment, 0x1b,
						0x1f, 0x21, "Tomato plant","Potato","Diseased tomato plant","Dead tomato plant").addProtectionItem(5478, 2).add(
						ProductType.VEGETABLE, 1982),
				new Plant("Sweetcorn", Plant.Sweetcorn, Patches.Allotment,
						0x22, 0x28, 0x2a,"Sweetcorn seed","Sweetcorn","Diseased sweetcorn plant","Dead sweetcorn plant").addProtectionItem(5931, 10).add(
						ProductType.VEGETABLE, 5986),
				new Plant("Strawberry", Plant.Strawberry, Patches.Allotment,
						0x2b, 0x32, 0x33,"Strawberry seed","Strawberry","Diseased strawberry","Dead strawberry").addProtectionItem(5386, 1).add(
						ProductType.VEGETABLE, 5504),
				new Plant("Watermelon", Plant.Watermelon, Patches.Allotment,
						0x34, 0x3c, 0x3e,"Watermelon seed","Watermelon","Diseased watermelon","Dead watermelon").addProtectionItem(2011, 10).add(
						ProductType.VEGETABLE, 5982),
				new Plant("Guam", Plant.Guam, Patches.Herb, 4, 8, 10,"Herbs","Herbs","Diseased herbs","Dead herbs").add(
						ProductType.GRIMY_HERB, 199).add(
						ProductType.CLEAN_HERB, 249),
				new Plant("Marrentil", Plant.Marrentil, Patches.Herb, 11, 15,
						17,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 201).add(
						ProductType.CLEAN_HERB, 251),
				new Plant("Tarromin", Plant.Tarromin, Patches.Herb, 18, 22, 24,"Herbs","Herbs","Diseased herbs","Dead herbs")
						.add(ProductType.GRIMY_HERB, 203).add(
								ProductType.CLEAN_HERB, 253),
				new Plant("Harralander", Plant.Harralander, Patches.Herb, 25,
						29, 31,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 205).add(
						ProductType.CLEAN_HERB, 255),
				new Plant("Ranarr", Plant.Ranarr, Patches.Herb, 32, 36, 38,"Herbs","Herbs","Diseased herbs","Dead herbs")
						.add(ProductType.GRIMY_HERB, 207).add(
								ProductType.CLEAN_HERB, 257),
				new Plant("Spirit weed", Plant.SpiritWeed, Patches.Herb, 0xcc,
						0xd0, 0xd7,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 12174).add(
						ProductType.CLEAN_HERB, 12172),
				new Plant("Toadflax", Plant.Toadflax, Patches.Herb, 39, 43, 45,"Herbs","Herbs","Diseased herbs","Dead herbs")
						.add(ProductType.GRIMY_HERB, 3049).add(
								ProductType.CLEAN_HERB, 2998),
				new Plant("Irit", Plant.Irit, Patches.Herb, 46, 50, 52,"Herbs","Herbs","Diseased herbs","Dead herbs").add(
						ProductType.GRIMY_HERB, 209).add(
						ProductType.CLEAN_HERB, 259),
				new Plant("Wergali", Plant.Wergali, Patches.Herb, 60, 65, 66,"Herbs","Herbs","Diseased herbs","Dead herbs")
						.add(ProductType.GRIMY_HERB, 14836).add(
								ProductType.CLEAN_HERB, 14854),
				new Plant("Avantoe", Plant.Avantoe, Patches.Herb, 53, 57, 59,"Herbs","Herbs","Diseased herbs","Dead herbs")
						.add(ProductType.GRIMY_HERB, 211).add(
								ProductType.CLEAN_HERB, 261),
				new Plant("Kwuarm", Plant.Kwuarm, Patches.Herb, 68, 72, 74,"Herbs","Herbs","Diseased herbs","Dead herbs")
						.add(ProductType.GRIMY_HERB, 213).add(
								ProductType.CLEAN_HERB, 263),
				new Plant("Snapdragon", Plant.Snapdragon, Patches.Herb, 75, 79,
						81,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 3051).add(
						ProductType.CLEAN_HERB, 3000),
				new Plant("Cadantine", Plant.Cadantine, Patches.Herb, 82, 86,
						88,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 215).add(
						ProductType.CLEAN_HERB, 265),
				new Plant("Lantadyme", Plant.Lantadyme, Patches.Herb, 89, 93,
						95,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 2485).add(
						ProductType.CLEAN_HERB, 2481),
				new Plant("Dwarf weed", Plant.DwarfWeed, Patches.Herb, 96, 100,
						102,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 217).add(
						ProductType.CLEAN_HERB, 267),
				new Plant("Torstol", Plant.Torstol, Patches.Herb, 103, 107, 109,"Herbs","Herbs","Diseased herbs","Dead herbs")
						.add(ProductType.GRIMY_HERB, 219).add(
								ProductType.CLEAN_HERB, 269),
				new Plant("Fellstalk", Plant.Fellstalk, Patches.Herb, 110, 114,
						116,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.GRIMY_HERB, 21626).add(
						ProductType.CLEAN_HERB, 21624), // ?!?!
				new Plant("Gout", Plant.GoutTuber, Patches.Herb, 0xc0, 0xc4,
						0xcb,"Herbs","Herbs","Diseased herbs","Dead herbs").add(ProductType.CLEAN_HERB, 3261)/*,
				// new Seed("Barley",5305,Patches.Hops,)
				// new Seed("Redberry",5101,Patches.Bush,0x05,0x09,0x0e),
				new Plant("Oak Tree", 5370, Patches.Tree, 0x08, 0x0c, 0x0e),
				new Plant("Willow Tree", 5371, Patches.Tree, 0x0f, 0x15, 0x17),
				new Plant("Maple Tree", 5372, Patches.Tree, 0x18, 0x20, 0x22),
				new Plant("Yew Tree", 5373, Patches.Tree, 0x23, 0x2d, 0x2f),
				new Plant("Magic Tree", 5374, Patches.Tree, 0x30, 0x3c, 0x3e)*/

		};
		for (Plant seed : seedsPlain) {
			seeds.put(seed.getId(), seed);
		}
		return seeds;
	}

	public static HashMap<Integer, Plant> seeds = loadSeeds();
}
