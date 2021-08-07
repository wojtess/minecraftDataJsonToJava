package me.wojtess;

import com.google.gson.*;

import java.io.*;
import java.util.*;

public class Main {

    static File output = new File("output");

    public static void main(String[] args) throws Exception {
        if(args.length >= 2) {
            output.mkdirs();
            switch (args[0]) {
                case "block":
                case "blocks": {
                    StringBuilder b = new StringBuilder();
                    Scanner sc = new Scanner(new File(args[1]));
                    while(sc.hasNext()) {
                        b.append(sc.next());
                    }
                    sc.close();
                    generateBlocks(b.toString());
                }
            }
        } else {
            System.out.println("java -jar program.jar <blocks> <path_to_file_with_json>");
        }
    }

    private static void generateBlocks(String json) throws IOException {
        JsonElement element = JsonParser.parseString(json);
        if(element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            StringBuilder builder = new StringBuilder();
            for (JsonElement jsonElement : array) {
                if(jsonElement.isJsonObject()) {
                    JsonObject j = jsonElement.getAsJsonObject();
                    int id = -1;
                    String displayName = "";
                    String name = "";
                    int hardness = 0;
                    int stackSize = 0;
                    boolean diggable = false;
                    String boundingBox = "block";
                    boolean transparent = false;
                    int emitLight = 0;
                    int filterLight = 0;
                    int resistance = 0;
                    String harvestTools = "new int[0]";
                    String material = "";
                    String variations = "new Variation[0]";
                    String drop = "new int[0]";

                    try {
                        id = j.get("id").getAsInt();
                    } catch (Exception ex) {}
                    try {
                        displayName = j.get("displayName").getAsString();
                    } catch (Exception ex) {}
                    try {
                        name = j.get("name").getAsString();
                    } catch (Exception ex) {}
                    try {
                        hardness = j.get("hardness").getAsInt();
                    } catch (Exception ex) {}
                    try {
                        stackSize = j.get("stackSize").getAsInt();
                    } catch (Exception ex) {}
                    try {
                        diggable = j.get("diggable").getAsBoolean();
                    } catch (Exception ex) {}
                    try {
                        boundingBox = j.get("boundingBox").getAsString();
                    } catch (Exception ex) {}
                    try {
                        JsonArray drops = j.getAsJsonArray("drops");
                        StringBuilder b = new StringBuilder("new int[] {");
                        for (int i = 0; i < drops.size(); i++) {
                            if (drops.get(i).isJsonObject()) {
                                JsonObject a = drops.get(i).getAsJsonObject();
                                if(a.get("drop").isJsonObject()) {
                                    b.append(a.get("drop").getAsJsonObject().get("id").getAsInt());
                                } else {
                                    b.append(a.get("drop").getAsInt());
                                }
                                if(i < drops.size() - 1) {
                                    b.append(',');
                                }
                            }
                        }
                        b.append('}');
                        drop = b.toString();
                    } catch (Exception ex) {}
                    try {
                        JsonArray variationsArray = j.getAsJsonArray("variations");
                        StringBuilder b = new StringBuilder("new Variation[] {");
                        for (int i = 0; i < variationsArray.size(); i++) {
                            if (variationsArray.get(i).isJsonObject()) {
                                JsonObject obj = variationsArray.get(i).getAsJsonObject();
                                b.append(String.format("new Variation(%s,\"%s\")",String.valueOf(obj.get("metadata").getAsInt()),obj.get("displayName").getAsString()));
                                if(i < variationsArray.size() - 1) {
                                    b.append(',');
                                }
                            }
                        }
                        b.append('}');
                        variations = b.toString();
                    } catch (Exception ex) {}

                    try {
                        transparent = j.get("transparent").getAsBoolean();
                    } catch (Exception ex) {}
                    try {
                        emitLight = j.get("emitLight").getAsInt();
                    } catch (Exception ex) {}
                    try {
                        filterLight = j.get("filterLight").getAsInt();
                    } catch (Exception ex) {}
                    try {
                        resistance = j.get("resistance").getAsInt();
                    } catch (Exception ex) {}

                    try {
                        List<Map.Entry<String, JsonElement>> harvestToolsObject = new ArrayList<>(j.getAsJsonObject("harvestTools").entrySet());
                        StringBuilder b = new StringBuilder("new int[] {");
                        for (int i = 0; i < harvestToolsObject.size(); i++) {
                            String s = harvestToolsObject.get(i).getKey();
                            try {
                                b.append(Integer.parseInt(s));
                            } catch (NumberFormatException ex) {
                            }
                            if(i < harvestToolsObject.size() - 1) {
                                b.append(',');
                            }
                        }
                        b.append('}');
                        harvestTools = b.toString();
                    } catch (Exception ex) {}
                    try {
                        material = j.get("material").getAsString();
                    } catch (Exception ex) {}

                    builder.append("         ");
                    builder.append(String.format("blocks.add(new Block(%s,\"%s\",\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,Material.valueOf(\"%s\")));\n",id,name,displayName,hardness,stackSize,diggable,boundingBox,drop,variations,transparent,emitLight,filterLight,resistance,harvestTools,material));
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            generateBlockClass(builder.toString());
        }
    }

    public static class Variation {
        private int metadata;
        private String displayName;

        public Variation(int metadata, String displayName) {
            this.metadata = metadata;
            this.displayName = displayName;
        }

        public void setMetadata(int metadata) {
            this.metadata = metadata;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    private static void generateBlockClass(String st) throws IOException {
        String blockClass = "import java.util.List;\n" +
                "\n" +
                "public class Block implements Cloneable {\n" +
                "    private final int id;\n" +
                "    private int metadata;\n" +
                "    private final String name;\n" +
                "    private final String displayName;\n" +
                "    private final int hardness;\n" +
                "    private final int stackSize;\n" +
                "    private final boolean diggable;\n" +
                "    private final AABB aabb;\n" +
                "    private final int[] drops;\n" +
                "    private final Variation[] variations;\n" +
                "    private final boolean transparent;\n" +
                "    private final int emitLight;\n" +
                "    private final int filterLight;\n" +
                "    private final int resistance;\n" +
                "    private final int[] harvestTool;\n" +
                "    private final Material material;\n" +
                "    private static List<Block> blocks;\n" +
                "\n" +
                "    static {\n" +
                "" + st + "\n" +
                "    }\n" +
                "\n" +
                "    public Block(int id, String name, String displayName, int hardness, int stackSize, boolean diggable, String aabb, int[] drops, Variation[] variations, boolean transparent, int emitLight, int filterLight, int resistance, int[] harvestTool, Material material) {\n" +
                "        this.id = id;\n" +
                "        this.name = name;\n" +
                "        this.displayName = displayName;\n" +
                "        this.hardness = hardness;\n" +
                "        this.stackSize = stackSize;\n" +
                "        this.diggable = diggable;\n" +
                "        if(aabb.equalsIgnoreCase(\"empty\")) {\n" +
                "            this.aabb = new AABB(0,0,0,0,0,0);\n" +
                "        } else if(aabb.equalsIgnoreCase(\"block\")) {\n" +
                "            this.aabb = new AABB(0,1,0,1,0,1);\n" +
                "        } else {\n" +
                "            this.aabb = new AABB(0,0,0,0,0,0);\n" +
                "        }\n" +
                "        this.drops = drops;\n" +
                "        this.variations = variations;\n" +
                "        this.transparent = transparent;\n" +
                "        this.emitLight = emitLight;\n" +
                "        this.filterLight = filterLight;\n" +
                "        this.resistance = resistance;\n" +
                "        this.harvestTool = harvestTool;\n" +
                "        this.material = material;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "\n" +
                "    public String getDisplayName() {\n" +
                "        return displayName;\n" +
                "    }\n" +
                "\n" +
                "    public int getHardness() {\n" +
                "        return hardness;\n" +
                "    }\n" +
                "\n" +
                "    public int getStackSize() {\n" +
                "        return stackSize;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isDiggable() {\n" +
                "        return diggable;\n" +
                "    }\n" +
                "\n" +
                "    public AABB getAabb() {\n" +
                "        return aabb;\n" +
                "    }\n" +
                "\n" +
                "    public int[] getDrops() {\n" +
                "        return drops;\n" +
                "    }\n" +
                "\n" +
                "    public Variation[] getVariations() {\n" +
                "        return variations;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isTransparent() {\n" +
                "        return transparent;\n" +
                "    }\n" +
                "\n" +
                "    public int getEmitLight() {\n" +
                "        return emitLight;\n" +
                "    }\n" +
                "\n" +
                "    public int getFilterLight() {\n" +
                "        return filterLight;\n" +
                "    }\n" +
                "\n" +
                "    public int getResistance() {\n" +
                "        return resistance;\n" +
                "    }\n" +
                "\n" +
                "    public int[] getHarvestTool() {\n" +
                "        return harvestTool;\n" +
                "    }\n" +
                "\n" +
                "    public Material getMaterial() {\n" +
                "        return material;\n" +
                "    }\n" +
                "\n" +
                "    public static List<Block> getBlocks() {\n" +
                "        return blocks;\n" +
                "    }\n" +
                "\n" +
                "    public int getId() {\n" +
                "        return id;\n" +
                "    }\n" +
                "\n" +
                "    public int getMetadata() {\n" +
                "        return metadata;\n" +
                "    }\n" +
                "\n" +
                "    public void setMetadata(int metadata) {\n" +
                "        this.metadata = metadata;\n" +
                "    }\n" +
                "\n" +
                "    public static class Variation {\n" +
                "        private int metadata;\n" +
                "        private String displayName;\n" +
                "\n" +
                "        public Variation(int metadata, String displayName) {\n" +
                "            this.metadata = metadata;\n" +
                "            this.displayName = displayName;\n" +
                "        }\n" +
                "\n" +
                "        public void setMetadata(int metadata) {\n" +
                "            this.metadata = metadata;\n" +
                "        }\n" +
                "\n" +
                "        public void setDisplayName(String displayName) {\n" +
                "            this.displayName = displayName;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public enum Material {\n" +
                "        ROCK,\n" +
                "        WOOD,\n" +
                "        PLANT,\n" +
                "        DIRT,\n" +
                "        LEAVES,\n" +
                "        WEB,\n" +
                "        WOOL\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected Block clone() {\n" +
                "        try {\n" +
                "            return (Block) super.clone();\n" +
                "        } catch (CloneNotSupportedException ex) {\n" +
                "            return null;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";


        String AABBClass = "public class AABB {\n" +
                "    private double minX;\n" +
                "    private double maxX;\n" +
                "    private double minY;\n" +
                "    private double maxY;\n" +
                "    private double minZ;\n" +
                "    private double maxZ;\n" +
                "\n" +
                "    public AABB(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {\n" +
                "        this.minX = minX;\n" +
                "        this.maxX = maxX;\n" +
                "        this.minY = minY;\n" +
                "        this.maxY = maxY;\n" +
                "        this.minZ = minZ;\n" +
                "        this.maxZ = maxZ;\n" +
                "    }\n" +
                "\n" +
                "    public AABB copy() {\n" +
                "        return new AABB(minX, maxX, minY, maxY, minZ, maxZ);\n" +
                "    }\n" +
                "\n" +
                "    public double getMinX() {\n" +
                "        return minX;\n" +
                "    }\n" +
                "\n" +
                "    public void setMinX(double minX) {\n" +
                "        this.minX = minX;\n" +
                "    }\n" +
                "\n" +
                "    public double getMaxX() {\n" +
                "        return maxX;\n" +
                "    }\n" +
                "\n" +
                "    public void setMaxX(double maxX) {\n" +
                "        this.maxX = maxX;\n" +
                "    }\n" +
                "\n" +
                "    public double getMinY() {\n" +
                "        return minY;\n" +
                "    }\n" +
                "\n" +
                "    public void setMinY(double minY) {\n" +
                "        this.minY = minY;\n" +
                "    }\n" +
                "\n" +
                "    public double getMaxY() {\n" +
                "        return maxY;\n" +
                "    }\n" +
                "\n" +
                "    public void setMaxY(double maxY) {\n" +
                "        this.maxY = maxY;\n" +
                "    }\n" +
                "\n" +
                "    public double getMinZ() {\n" +
                "        return minZ;\n" +
                "    }\n" +
                "\n" +
                "    public void setMinZ(double minZ) {\n" +
                "        this.minZ = minZ;\n" +
                "    }\n" +
                "\n" +
                "    public double getMaxZ() {\n" +
                "        return maxZ;\n" +
                "    }\n" +
                "\n" +
                "    public void setMaxZ(double maxZ) {\n" +
                "        this.maxZ = maxZ;\n" +
                "    }\n" +
                "}";

        writeToFile(new File(output,"Block.java"),blockClass);
        writeToFile(new File(output,"AABB.java"),AABBClass);
    }

    private static void writeToFile(File file,String str) throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(str);
        writer.close();
    }

}
