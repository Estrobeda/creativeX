/*
 * This file is part of creative, licensed under the MIT license
 *
 * Copyright (c) 2021-2025 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.creative.serialize.minecraft.item;

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.DyeColor;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.ItemModelTransformation;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.property.ItemBooleanProperty;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;
import team.unnamed.creative.item.special.BedSpecialRender;
import team.unnamed.creative.item.special.SpecialRender;

import static org.junit.jupiter.api.Assertions.*;

class ItemSerializationTest {


    @Test
    void test_air_deserialization() throws Exception {
        final @Language("JSON") String input = "{\n" +
                "  \"model\": {\n" +
                "    \"type\": \"minecraft:model\",\n" +
                "    \"model\": \"minecraft:item/air\"\n" +
                "  }\n" +
                "}";

        final Item item = ItemSerializer.INSTANCE.deserializeFromJsonString(input, Key.key("minecraft", "air"));

        assertEquals(Key.key("minecraft", "air"), item.key());
        assertEquals(Item.DEFAULT_HAND_ANIMATION_ON_SWAP, item.handAnimationOnSwap());
        assertEquals(Item.DEFAULT_OVERSIZED_IN_GUI, item.oversizedInGui());

        final ItemModel model = item.model();
        assertInstanceOf(ReferenceItemModel.class, model);

        final ReferenceItemModel reference = (ReferenceItemModel) model;
        assertEquals(Key.key("minecraft:item/air"), reference.model());
        assertEquals(0, reference.tints().size());
    }

    @Test
    void test_air_serialization() throws Exception {
        final Item item = Item.item(Key.key("minecraft", "air"), ItemModel.reference(Key.key("minecraft:item/air")), false, true);

        final String expected = "{\"model\":{\"type\":\"model\",\"model\":\"item/air\",\"tints\":[]},\"hand_animation_on_swap\":false,\"oversized_in_gui\":true}";

        assertEquals(
                expected,
                ItemSerializer.INSTANCE.serializeToJsonString(item)
        );
    }

    @Test
    void test_java_26_item_model_transformation_serialization() throws Exception {
        final Item item = Item.item(
                Key.key("minecraft", "apple"),
                ItemModel.reference(
                        Key.key("minecraft:item/apple"),
                        java.util.Collections.emptyList(),
                        ItemModelTransformation.decomposed(
                                new Vector3Float(0.5F, 0.5F, 0.5F),
                                new Vector3Float(1F, 1F, 1F),
                                new float[]{0F, 0F, 0F, 1F},
                                new float[]{0F, 0F, 0F, 1F}
                        )
                )
        );

        assertEquals(
                "{\"model\":{\"type\":\"model\",\"model\":\"item/apple\",\"tints\":[],\"transformation\":{\"translation\":[0.5,0.5,0.5],\"scale\":[1.0,1.0,1.0],\"left_rotation\":[0.0,0.0,0.0,1.0],\"right_rotation\":[0.0,0.0,0.0,1.0]}}}",
                ItemSerializer.INSTANCE.serializeToJsonString(item)
        );
    }

    @Test
    void test_java_26_special_model_fields_serialization() throws Exception {
        final Item item = Item.item(
                Key.key("minecraft", "red_bed"),
                ItemModel.composite(
                        ItemModel.special(
                                SpecialRender.bed(Key.key("minecraft:red"), BedSpecialRender.Part.HEAD),
                                Key.key("minecraft:item/red_bed")
                        ),
                        ItemModel.special(
                                SpecialRender.banner(DyeColor.RED, team.unnamed.creative.item.special.BannerSpecialRender.Attachment.WALL),
                                Key.key("minecraft:item/red_banner")
                        ),
                        ItemModel.special(
                                SpecialRender.bell(),
                                Key.key("minecraft:item/bell")
                        ),
                        ItemModel.special(
                                SpecialRender.book(45F, 0.2F, 0.8F),
                                Key.key("minecraft:item/book")
                        ),
                        ItemModel.special(
                                SpecialRender.endCube(team.unnamed.creative.item.special.EndCubeSpecialRender.Effect.GATEWAY),
                                Key.key("minecraft:item/end_cube")
                        )
                )
        );

        assertEquals(
                "{\"model\":{\"type\":\"composite\",\"models\":[{\"type\":\"special\",\"model\":{\"type\":\"bed\",\"texture\":\"red\",\"part\":\"head\"},\"base\":\"item/red_bed\"},{\"type\":\"special\",\"model\":{\"type\":\"banner\",\"color\":\"red\",\"attachment\":\"wall\"},\"base\":\"item/red_banner\"},{\"type\":\"special\",\"model\":{\"type\":\"bell\"},\"base\":\"item/bell\"},{\"type\":\"special\",\"model\":{\"type\":\"book\",\"open_angle\":45.0,\"page1\":0.2,\"page2\":0.8},\"base\":\"item/book\"},{\"type\":\"special\",\"model\":{\"type\":\"end_cube\",\"effect\":\"gateway\"},\"base\":\"item/end_cube\"}]}}",
                ItemSerializer.INSTANCE.serializeToJsonString(item)
        );
    }

    @Test
    void test_local_time_uses_time_zone_serialization() throws Exception {
        final Item item = Item.item(
                Key.key("minecraft", "clock"),
                ItemModel.select(
                        ItemStringProperty.localTime("en_us", "Europe/Stockholm", "HH:mm"),
                        SelectItemModel.Case._case(ItemModel.reference(Key.key("minecraft:item/clock")), "12:00")
                )
        );

        assertEquals(
                "{\"model\":{\"type\":\"select\",\"property\":\"local_time\",\"pattern\":\"HH:mm\",\"locale\":\"en_us\",\"time_zone\":\"Europe/Stockholm\",\"cases\":[{\"when\":\"12:00\",\"model\":{\"type\":\"model\",\"model\":\"item/clock\",\"tints\":[]}}]}}",
                ItemSerializer.INSTANCE.serializeToJsonString(item)
        );
    }

    @Test
    void test_local_time_accepts_legacy_timezone_deserialization() throws Exception {
        final @Language("JSON") String input = "{\"model\":{\"type\":\"select\",\"property\":\"local_time\",\"pattern\":\"HH:mm\",\"locale\":\"en_us\",\"timezone\":\"Europe/Stockholm\",\"cases\":[{\"when\":\"12:00\",\"model\":{\"type\":\"model\",\"model\":\"item/clock\"}}]}}";

        final Item item = ItemSerializer.INSTANCE.deserializeFromJsonString(input, Key.key("minecraft", "clock"));

        assertEquals(
                Item.item(
                        Key.key("minecraft", "clock"),
                        ItemModel.select(
                                ItemStringProperty.localTime("en_us", "Europe/Stockholm", "HH:mm"),
                                SelectItemModel.Case._case(ItemModel.reference(Key.key("minecraft:item/clock")), "12:00")
                        )
                ),
                item
        );
    }

    @Test
    void test_component_condition_serialization() throws Exception {
        final Item item = Item.item(
                Key.key("minecraft", "sword"),
                ItemModel.conditional(
                        ItemBooleanProperty.component("custom_data", "{\"rarity\":\"legendary\"}"),
                        ItemModel.reference(Key.key("minecraft:item/legendary_sword")),
                        ItemModel.reference(Key.key("minecraft:item/sword"))
                )
        );

        assertEquals(
                "{\"model\":{\"type\":\"condition\",\"property\":\"component\",\"predicate\":\"custom_data\",\"value\":{\"rarity\":\"legendary\"},\"on_true\":{\"type\":\"model\",\"model\":\"item/legendary_sword\",\"tints\":[]},\"on_false\":{\"type\":\"model\",\"model\":\"item/sword\",\"tints\":[]}}}",
                ItemSerializer.INSTANCE.serializeToJsonString(item)
        );
    }

    @Test
    void test_component_condition_deserialization() throws Exception {
        final @Language("JSON") String input = "{\"model\":{\"type\":\"condition\",\"property\":\"component\",\"predicate\":\"custom_data\",\"value\":{\"rarity\":\"legendary\"},\"on_true\":{\"type\":\"model\",\"model\":\"item/legendary_sword\"},\"on_false\":{\"type\":\"model\",\"model\":\"item/sword\"}}}";

        final Item item = ItemSerializer.INSTANCE.deserializeFromJsonString(input, Key.key("minecraft", "sword"));

        assertEquals(
                Item.item(
                        Key.key("minecraft", "sword"),
                        ItemModel.conditional(
                                ItemBooleanProperty.component("custom_data", "{\"rarity\":\"legendary\"}"),
                                ItemModel.reference(Key.key("minecraft:item/legendary_sword")),
                                ItemModel.reference(Key.key("minecraft:item/sword"))
                        )
                ),
                item
        );
    }

    @Test
    void test_component_select_property_serialization() throws Exception {
        final Item item = Item.item(
                Key.key("minecraft", "wolf_armor"),
                ItemModel.select(
                        ItemStringProperty.component("minecraft:wolf/collar"),
                        SelectItemModel.Case._case(ItemModel.reference(Key.key("minecraft:item/lime_wolf_armor")), "lime")
                )
        );

        assertEquals(
                "{\"model\":{\"type\":\"select\",\"property\":\"component\",\"component\":\"minecraft:wolf/collar\",\"cases\":[{\"when\":\"lime\",\"model\":{\"type\":\"model\",\"model\":\"item/lime_wolf_armor\",\"tints\":[]}}]}}",
                ItemSerializer.INSTANCE.serializeToJsonString(item)
        );
    }

    @Test
    void test_component_select_property_deserialization() throws Exception {
        final @Language("JSON") String input = "{\"model\":{\"type\":\"select\",\"property\":\"component\",\"component\":\"minecraft:wolf/collar\",\"cases\":[{\"when\":\"lime\",\"model\":{\"type\":\"model\",\"model\":\"item/lime_wolf_armor\"}}]}}";

        final Item item = ItemSerializer.INSTANCE.deserializeFromJsonString(input, Key.key("minecraft", "wolf_armor"));

        assertEquals(
                Item.item(
                        Key.key("minecraft", "wolf_armor"),
                        ItemModel.select(
                                ItemStringProperty.component("minecraft:wolf/collar"),
                                SelectItemModel.Case._case(ItemModel.reference(Key.key("minecraft:item/lime_wolf_armor")), "lime")
                        )
                ),
                item
        );
    }

    @Test
    void test_bow_deserialization() throws Exception {
        final @Language("JSON") String input = "{\n" +
                "  \"hand_animation_on_swap\": false,\n" +
                "  \"oversized_in_gui\": true,\n" +
                "  \"model\": {\n" +
                "    \"type\": \"minecraft:condition\",\n" +
                "    \"on_false\": {\n" +
                "      \"type\": \"minecraft:model\",\n" +
                "      \"model\": \"minecraft:item/bow\"\n" +
                "    },\n" +
                "    \"on_true\": {\n" +
                "      \"type\": \"minecraft:range_dispatch\",\n" +
                "      \"entries\": [\n" +
                "        {\n" +
                "          \"model\": {\n" +
                "            \"type\": \"minecraft:model\",\n" +
                "            \"model\": \"minecraft:item/bow_pulling_1\"\n" +
                "          },\n" +
                "          \"threshold\": 0.65\n" +
                "        },\n" +
                "        {\n" +
                "          \"model\": {\n" +
                "            \"type\": \"minecraft:model\",\n" +
                "            \"model\": \"minecraft:item/bow_pulling_2\"\n" +
                "          },\n" +
                "          \"threshold\": 0.9\n" +
                "        }\n" +
                "      ],\n" +
                "      \"fallback\": {\n" +
                "        \"type\": \"minecraft:model\",\n" +
                "        \"model\": \"minecraft:item/bow_pulling_0\"\n" +
                "      },\n" +
                "      \"property\": \"minecraft:use_duration\",\n" +
                "      \"scale\": 0.05\n" +
                "    },\n" +
                "    \"property\": \"minecraft:using_item\"\n" +
                "  }\n" +
                "}";
        final Item item = ItemSerializer.INSTANCE.deserializeFromJsonString(input, Key.key("minecraft", "bow"));

        assertEquals(Key.key("minecraft", "bow"), item.key());
        assertFalse(item.handAnimationOnSwap());
        assertTrue(item.oversizedInGui());

        final ItemModel model = item.model();
        assertInstanceOf(ConditionItemModel.class, model);

        final ConditionItemModel condition = (ConditionItemModel) model;
        final ItemBooleanProperty property = condition.condition();

        assertEquals(ItemBooleanProperty.usingItem(), property);

        final ItemModel onFalse = condition.onFalse();
        assertInstanceOf(ReferenceItemModel.class, onFalse);

        final ReferenceItemModel reference = (ReferenceItemModel) onFalse;
        assertEquals(Key.key("minecraft:item/bow"), reference.model());
        assertEquals(0, reference.tints().size());

        final ItemModel onTrue = condition.onTrue();
        assertInstanceOf(RangeDispatchItemModel.class, onTrue);

        final RangeDispatchItemModel rangeDispatch = (RangeDispatchItemModel) onTrue;
        assertEquals(ItemNumericProperty.useDuration(), rangeDispatch.property());
        assertEquals(0.05f, rangeDispatch.scale());

        final ItemModel fallback = rangeDispatch.fallback();
        assertInstanceOf(ReferenceItemModel.class, fallback);

        final ReferenceItemModel fallbackReference = (ReferenceItemModel) fallback;
        assertEquals(Key.key("minecraft:item/bow_pulling_0"), fallbackReference.model());
        assertEquals(0, fallbackReference.tints().size());

        assertEquals(2, rangeDispatch.entries().size());

        final RangeDispatchItemModel.Entry entry1 = rangeDispatch.entries().get(0);
        assertEquals(0.65f, entry1.threshold());

        final ItemModel model1 = entry1.model();
        assertInstanceOf(ReferenceItemModel.class, model1);

        final ReferenceItemModel reference1 = (ReferenceItemModel) model1;
        assertEquals(Key.key("minecraft:item/bow_pulling_1"), reference1.model());
        assertEquals(0, reference1.tints().size());

        final RangeDispatchItemModel.Entry entry2 = rangeDispatch.entries().get(1);
        assertEquals(0.9f, entry2.threshold());

        final ItemModel model2 = entry2.model();
        assertInstanceOf(ReferenceItemModel.class, model2);

        final ReferenceItemModel reference2 = (ReferenceItemModel) model2;
        assertEquals(Key.key("minecraft:item/bow_pulling_2"), reference2.model());
        assertEquals(0, reference2.tints().size());
    }


}
