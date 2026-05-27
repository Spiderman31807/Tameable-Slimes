package tameable.slimes;

import tameable.slimes.init.TameableSlimesModItems;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

public class Color {
	public static final List<Integer> Values = List.of(16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001);
	public static final List<String> Names = List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");
	public static final int slime = 7586402;

	public CompoundTag colorTag = getLongTag(Values.get(0));
	public int[] colorRGB = fromLong(Values.get(0));
	public int colorLong = Values.get(0);
	public boolean invaildInput = false;
	
	public static int toLong(int red, int green, int blue) {
		return (red * 256 * 256) + (green * 256) + blue;
	}
	
	public static int toLong(int[] color) {
		return toLong(color[0], color[1], color[2]);
	}
	
	public static int[] fromLong(int color) {
    	int blue = color / 65536;
    	int green = (color % 65536) / 256;
    	int red = color % 256;
    	return new int[] { blue, green, red };
	}

	public Color() {
	}

	public Color(DyeItem color) {
		this.setDye(color);
	}

	public Color(DyeColor color) {
		this.setDye(color);
	}

	public Color(CompoundTag color) {
		this.setTag(color);
	}

	public Color(ItemStack stack) {
		this.setTag(stack.getTag());
	}

	public Color(int[] color) {
		this.setRGB(color);
	}

	public Color(int color) {
		this.setLong(color);
	}

	public CompoundTag getTag() {
		return this.colorTag;
	}
	
	public int[] getRGB() {
		return this.colorRGB;
	}

	public void setDye(DyeItem color) {
		this.setDye(color.getDyeColor());
	}

	public void setDye(DyeColor color) {
		if(color != null)
			this.setLong(Values.get(Names.indexOf(color.getName())));
	}

	public DyeColor getDye() {
		if(Values.contains(this.colorLong))
			return DyeColor.byName(Names.get(Values.indexOf(this.colorLong)), null);
		return getNearestDye();
	}

	public DyeColor getNearestDye() {
		Map<DyeColor, Double> DistanceMap = new HashMap<>();
		Map<Double, DyeColor> ColorMap = new HashMap<>();
		for(int value : Values) {
			DyeColor color = DyeColor.byName(Names.get(Values.indexOf(value)), null);
			if(color == null)
				continue;
			double distance = getLikeness(this, new Color(color));
			DistanceMap.put(color, distance);
			ColorMap.put(distance, color);
		}
		
		if(DistanceMap.isEmpty())
			return null;
			
		List<Double> distances = new ArrayList(DistanceMap.values());
		distances.sort(Double::compareTo);
		return ColorMap.get(distances.get(0));
	}

	public static double getLikeness(Color color1, Color color2) {
		int red1 = color1.getRGB()[0];
		int red2 = color2.getRGB()[0];
		int green1 = color1.getRGB()[1];
		int green2 = color2.getRGB()[1];
		int blue1 = color1.getRGB()[2];
		int blue2 = color2.getRGB()[2];

		double red = (red1 - red2) * (red1 - red2);
		double green = (green1 - green2) * (green1 - green2);
		double blue = (blue1 - blue2) * (blue1 - blue2);
		return Math.sqrt(red + green + blue);
	}

	public ItemStack getSlimeType() {
		ItemLike slimeItem = switch(this.getDye()) {
			default -> Items.SLIME_BALL;
			case WHITE -> TameableSlimesModItems.WHITE_SLIME.get();
			case ORANGE -> TameableSlimesModItems.ORANGE_SLIME.get();
			case MAGENTA -> TameableSlimesModItems.MAGENTA_SLIME.get();
			case LIGHT_BLUE -> TameableSlimesModItems.LIGHT_BLUE_SLIME.get();
			case LIME -> TameableSlimesModItems.LIME_SLIME.get();
			case PINK -> TameableSlimesModItems.PINK_SLIME.get();
			case GRAY -> TameableSlimesModItems.GRAY_SLIME.get();
			case LIGHT_GRAY -> TameableSlimesModItems.LIGHT_GRAY_SLIME.get();
			case CYAN -> TameableSlimesModItems.CYAN_SLIME.get();
			case PURPLE -> TameableSlimesModItems.PURPLE_SLIME.get();
			case BLUE -> TameableSlimesModItems.BLUE_SLIME.get();
			case BROWN -> TameableSlimesModItems.BROWN_SLIME.get();
			case GREEN -> TameableSlimesModItems.GREEN_SLIME.get();
			case RED -> TameableSlimesModItems.RED_SLIME.get();
			case BLACK -> TameableSlimesModItems.BLACK_SLIME.get();
		};
		
		return new ItemStack(slimeItem);
	}
	
	public float[] getAFloat() {
		float red = (float)(this.colorRGB[0] / 255);
		float green = (float)(this.colorRGB[1] / 255);
		float blue = (float)(this.colorRGB[2] / 255);
		return new float[] { red, green, blue }; 
	}
	
	/*public void setAFloat(float[] aFloat) {
		this.setRGB(new int[] { aFloat[0] * 255, aFloat[1] * 255, aFloat[2] * 255 }); 
	}*/
	
	public int getLong() {
		return this.colorLong;
	}

	public CompoundTag getDefault(CompoundTag Default) {
		if(this.invaildInput == true)
			return Default;
		return this.colorTag;
	}
	
	public int[] getDefault(int[] Default) {
		if(this.invaildInput == true)
			return Default;
		return this.colorRGB;
	}
	
	public int getDefault(int Default) {
		if(this.invaildInput == true)
			return Default;
		return this.colorLong;
	}

	public void setDefault(int Default) {
		if(this.getDefault(Default) != this.colorLong)
			this.setLong(this.getDefault(Default));
	}

	public void setDefault(int[] Default) {
		this.setDefault(toLong(Default));
	}

	public void setDefault(CompoundTag Default) {
		this.setDefault(getTagLong(Default));
	}

	public boolean setTag(CompoundTag color) {
		if(!verifyTag(color)) {
			this.invaildInput = true;
			return false;
		}
			
		this.colorTag = color;
		this.colorRGB = getTagRGB(color);
		this.colorLong = getTagLong(color);
		this.invaildInput = false;
		return true;
	}

	public boolean setTag(ItemStack stack) {
		return setTag(stack.getTag());
	}

	public void setRGB(int red, int green, int blue) {
		this.setRGB(new int[] { red, green, blue });
		this.invaildInput = false;
	}

	public void setRGB(int[] color) {
		this.colorRGB = color;
		this.colorTag = getRGBTag(color);
		this.colorLong = toLong(color);
		this.invaildInput = false;
	}

	public void setLong(int color) {
		this.colorLong = color;
		this.colorTag = getLongTag(color);
		this.colorRGB = fromLong(color);
		this.invaildInput = false;
	}

	public static int getTagLong(CompoundTag compound) {
		if(compound.contains("display"))
			return getTagLong(compound.getCompound("display"));
		if(compound.contains("color"))
			return compound.getInt("color");
		return Values.get(0);
	}

	public static int[] getTagRGB(CompoundTag compound) {
		return fromLong(getTagLong(compound));
	}

	public static CompoundTag getRGBTag(int[] RGB) {
    	return getLongTag(toLong(RGB));
	}

	public static CompoundTag getRGBTag(int red, int green, int blue) {
    	return getLongTag(toLong(red, green, blue));
	}

	public static CompoundTag getLongTag(int Long) {
    	CompoundTag compound = new CompoundTag();
    	compound.putInt("color", Long);

    	CompoundTag Color = new CompoundTag();
    	Color.put("display", compound);
    	return Color;
	}

	public void applyColor(ItemStack stack) {
		CompoundTag compound = stack.getOrCreateTag();
		compound.put("display", this.colorTag.getCompound("display"));
		stack.setTag(compound);
	}

	public static void applyColor(Color color, ItemStack stack) {
		color.applyColor(stack);
	}

    public static float[] toFloat(Color color, float multiplier) {
    	int[] values = color.getRGB();
    	float[] RGB = new float[] { values[0], values[1], values[2] };
    	return new float[] { RGB[0] * multiplier, RGB[1] * multiplier, RGB[2] * multiplier };
    }

	public static boolean verifyTag(CompoundTag compound) {
		if(compound == null)
			return false;
		if(compound.contains("display"))
			return verifyTag(compound.getCompound("display"));
		return compound.contains("color");
	}

	public Color mix(Color color) {
		ArrayList blendColors = new ArrayList();
		blendColors.add(this);
		blendColors.add(color);
		return blend(blendColors);
	}

	public Color mix(List<Color> colors) {
		ArrayList blendColors = new ArrayList(colors);
		blendColors.add(this);
		return blend(blendColors);
	}

	public static Color blend(List<Color> colors) {
    	if (colors == null || colors.size() <= 0)
        	return null;
    	float ratio = 1f / ((float) colors.size());

    	int a = 0;
    	int r = 0;
    	int g = 0;
    	int b = 0;

    	for (int i = 0; i < colors.size(); i++) {
        	int rgb = colors.get(i).getLong();
        	int a1 = (rgb >> 24 & 0xff);
        	int r1 = ((rgb & 0xff0000) >> 16);
        	int g1 = ((rgb & 0xff00) >> 8);
        	int b1 = (rgb & 0xff);
        	a += ((int) a1 * ratio);
        	r += ((int) r1 * ratio);
        	g += ((int) g1 * ratio);
        	b += ((int) b1 * ratio);
    	}

    	return new Color(a << 24 | r << 16 | g << 8 | b);
	}

	public static int minLight(int color) {
		return minLight(new Color(color));
	}

	public static int minLight(Color color) {
		int[] RGB = color.getRGB();
		if(RGB[0] < 29 && RGB[1] < 29 && RGB[2] < 33)
			return 1908001;
		return color.getLong();
	}
}
