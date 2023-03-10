package com.ebicep.warlords.util.pve;

import javax.annotation.Nonnull;

// https://minecraft-heads.com/
//
public enum SkullID {
    // https://minecraft-heads.com/custom-heads/humanoid/50492-ethereal-wither-skull
    ETHEREAL_WITHER_SKULL(
            "c415a3d8-b9a0-48b7-b170-a447634e1b0c",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjExNzc3OTE2ZTc1NjQ0M2UzMGEwNDNmMmJjOWNjZTBkMGZlNjI0YmQ2MTkyZDdiYTIzZjk2YTFkNzFiOWYzZiJ9fX0=="
    ),
    // https://prnt.sc/jxuV7HjNusRu
    GRADIENT_SOUL(
            "e6ebaea8-d746-4bf2-8509-5b0949bf023a",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjdiZGJiZGY5NDg2ZmQxNzY5ZDE4YWU5MDJmMzQzYjgzYzUwMGE0YjM1ODQ3YmZkNDFmMmFhYWU0YmY3NTFiZSJ9fX0="
    ),
    // https://prnt.sc/bzEAAy8vBVcx
    CELESTIAL_GOLDOR(
            "f9db3132-5e7a-4f34-9a8d-bb8d5889b785",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU0MDZmNGIxMzExZWNjOTk4YjRkNzc3NTU3OWQ4Y2M1MTg0NmZhNjZmMmU0Yjc2MzdmNTU5M2RkOGJjZGMxZSJ9fX0="
    ),
    // https://prnt.sc/5tg_uT72qzUS
    BOW_HEAD(
            "e09979b1-9eca-40ba-9fcc-0a0590c928c1",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzU0Nzk1MTA0MjJiMWM1ZGNjNzdmNzVmZGMzMzQ2ZWQ0ZDlkYmJjYzFlODg1YjRhMjk5MmEyNzM3MzM2NDZhOSJ9fX0="
    ),
    // https://prnt.sc/w-EpPrSo7Gcl
    SWORD_HEAD(
            "8dcc05a3-aaa6-47c9-b6d7-39b9b03cfbd9",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjAwZTdiMzNlZTJhNjAwMjc1OGFjZmUwOGM3ZGY2YmQzN2E0OTdkYzlmODAwMGMzY2E5ODI0YTJjZmFiY2FkZCJ9fX0="
    ),
    HOODED_KNIGHT(
            "12269b6b-14dc-48cc-8174-80a9b7b1b01b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDg4ZWI0YjRjYWZhMWYzY2JlMDZjZjBlOTAzMGFkNzVjMjhiYjUwMGU4MDBiNjE5NmJkZjMyOTg2NWE1YzcxOCJ9fX0="
    ),
    SLIME_BLOCK(
            "fbd1a375-3fdd-47c9-8c8d-fb3b3f071841",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNmYmVhMjg3YjNhNjcyZWUzMjRjNzIwZTc3YWY4ZjczMGY4NTFkMjBkYWQ5ZmYxZmExYzA1MWVkZTViYzgxMyJ9fX0="
    ),
    // https://prnt.sc/GrnNWh_RjmbP
    GREEN_LANCER(
            "2ac0010e-e222-4005-be95-fd8a13762fa5",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUxMDg3YzBjNTE5YTlhMWRjZWU0MzI1NDEwYjE5YTFiZTQ4NTVlYWM1YTY2MmFlMWI1MjMzMjlmOTBmYWVjZCJ9fX0="
    ),
    // https://prnt.sc/yoyTcYEz37zz
    DEEP_DARK_WORM(
            "05e0d5e6-7bd0-4741-90c3-0ca105f67166",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg1NDRlNTQzMWRmMGZhMzM5MTkxYjMwY2Q1MGI3ZDJlZmRjNmU3NDQ1OTJhMWRmZjMwZTA1MjM0NzhiZTc1MSJ9fX0="
    ),
    // https://prnt.sc/dcDIDCrDmpfW
    WHITE_SHEKEL(
            "5ed2d133-c718-4017-ab7b-91aa7d38c1b4",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjk5ZDdmN2JhNTk2MWY3NmFlMmNlY2E5MmU2OTYxZGY3NjVjNzFiZmEwY2VjYzVhZmM0ZWZjOTNiZDczN2RlZSJ9fX0="
    ),
    // https://prnt.sc/EMu8m5b3jvs6
    SHADOW_DEMON(
            "532a3e28-b73c-4603-b639-752cb46e54ac",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE4NDMwY2I2YTE1MDZkMDdlY2I3Y2M0OTgzODJjMWZkN2U3YWNiZTk4YmMwNTQzNDhlNzg1ZjA4YzIyMzJmOCJ9fX0="
    ),
    // https://prnt.sc/h0fCnAMwuAop
    NETHERITE_HELMET(
            "de510138-9236-46d8-ba76-733bdac5b53a",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYWI0N2FlODhlOTQ5ZDJlZTJkNjZkOGUyY2VjZDZjNjNiMzM2OWNkY2YyYzQ3YWUxZDVlMTc3OTRjYjMzOCJ9fX0="
    ),
    // https://prnt.sc/KlIfvUXVDDp8
    SCULK_CORRUPTION(
            "c1a2f7d2-be42-43f0-83e7-1e84ddd53882",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTAxYzY3MmJiNDFiYzJmNTE2ODkwYzY2ZjM5NjIxNTRlMTQ2MDAzOGNhODM4MzdjOWUxNzdiM2M4ZTNjZDkwYyJ9fX0="
    ),
    // https://prnt.sc/lEMITadLJdty
    BLUE_GHOST(
            "98858a42-f28d-4708-965b-c4987f5ad104",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmViMTBjYjg3NTNkYTgwMDMwMzIwYmUyMzg5MWExM2ZmYzI4MmQ4NWU2ZDJiNzg2YmNlZjRlYmYyMzFhZDJlYSJ9fX0="
    ),
    // https://prnt.sc/QsBnWM03Uitp
    PURPLE_RIFT(
            "9516ba4d-7199-480e-8e15-f81569054612",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWMwODk0OTMxYTUxMDM4N2U0ZjYxMzgyOGYwMjM5Y2E0ZDkwODUzNDk5NjM3ZjYwNzlkMzkzMjdmMTQ2ZjBlOSJ9fX0="
    ),
    // https://prnt.sc/GW9JTDWANHjI
    LAVA_MONSTER(
            "0cacd58d-1100-41b3-938a-ec36b235d1bd",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWM0OWE0NDFmNjFhYmYzZGM0M2UzNzUzOWVmM2E5YzQ1MTQwYmYzNzQyMTM5ZmY1MzY3OGU5MDJmNTczNzE0MiJ9fX0="
    ),
    // https://prnt.sc/U4ngXQYKVS4j
    IRON_QUEEN(
            "e857e0bd-cccc-431a-8df2-fda9a0d1b79b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTYyY2ZhZjM4ZGVhZWUzZDA4ZDk2NWNlNmRjNTgwYzhjY2Q3NWQzYjE0ZDFiZmViNGUwOTM3MjZiNmM3YjFlMiJ9fX0="
    ),
    // https://prnt.sc/COd07J8s4EIV
    DEMON_KING(
            "ddfa4368-e57b-4b0d-a891-303d6ce20d4e",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk1NzE0ZjhhMDRiNTAyOWU3OWE5MDA3NmNlNTNiNGFkZGI2OTc4YThkYmUzMjAzMTIzMDc2Zjk5ZGQ1NDdiYiJ9fX0="
    ),
    // https://prnt.sc/qPX8fc4YJ4Ho
    DEMON_SKELETON(
            "eed60a6f-1249-4665-b191-94f847d684ef",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhNzdjNjY1OTU4ODQ0MDBmZTgxNzU2MWZkOGE0M2ZmOTkyYjQ1NjVlZjZjNWI1ZWQzMmRkMzkwOTVjOGIwMiJ9fX0="
    ),
    // https://prnt.sc/wZYR8rgfqTUj
    BURNING_WITHER_SKELETON(
            "e307413e-a49b-4fd6-acae-554e80015efa",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTFmNGY0ZGMzYjA5ODQ4MWQ4MTc1ZDg0ODkyYzE0NjM3ODU5YWNlNmI1MjQxN2Q0ZWRmZWRmNThlNmRiNTRmOSJ9fX0="
    ),
    // https://prnt.sc/6xLQRUy0lPko
    RED_EYE(
            "f327a9fc-26d1-4e2f-84c1-18741c587adf",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBhYzkwNzcxNzM5MzU4MDVhMzc5OTAzYmQ1OGQxMjc0NGViNDQyNjcwYmE5ZTAwNDMzMTI4ZDFjZDUyNjA5ZSJ9fX0="
    ),
    // https://prnt.sc/S2-LbZk0XORu
    DEMON(
            "db805929-f851-4067-82a9-f9c4fef1d42b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNGIwMmY3MjdkMjBmYmU0NzlkMTUxMzk4NWEyZTRkM2ExYTQ5MzA5ZGYwNDk2ZDczZDBhNDE0ZDA4ODdlMCJ9fX0="
    ),
    END_MONSTER(
            "b32a6652-bedc-4ae5-b873-9f105b787e95",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzUyZjg3MzllZGM5OWNmMWYwM2NmZDdhYTI4ODQyNzA0YTVlOWMwZjMxMTNiMTE4OGE2MzFjYmFmNWY3ZmY5ZSJ9fX0="
    ),
    SAMURAI(
            "3b53cfeb-2baf-4b11-ae51-fcc8fd08027b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzUyZjg3MzllZGM5OWNmMWYwM2NmZDdhYTI4ODQyNzA0YTVlOWMwZjMxMTNiMTE4OGE2MzFjYmFmNWY3ZmY5ZSJ9fX0="
    ),
    FACELESS_BANDIT(
            "10e5ac9b-3c3b-4b2d-98f9-57ccc30e237e",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VlZTJjYjQxY2VkZTVhYTQ0MTE3MTYyNGUxZTFlMzg4YjgyNjJhNGEwYmI5ZGZiZmQ4ODljYTAyYzQxY2IifX19="
    ),
    FACELESS_MAGE(
            "b31f22fd-3cd3-4183-836b-d98d003df922",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFiOGI2ZDA4YjRhMTdlYjVmMTlkYTNlNTI4MzczYTBkNmQzNjA5ZTEzZmU0OWRjMDIwMDkxNDQ3NWQ4MjNhZiJ9fX0="
    ),
    PURPLE_KNIGHT(
            "c8f8e39e-f616-4564-bdb0-797c7b1c98e3",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEwM2IzNWQ0NDg1MGNiNDJiMDAwMTdhZGRiN2Y4NWVhYWMyNGI1NmEwY2Q1MWNhMWNhYzIyYjZlYjQyM2UxMSJ9fX0="
    ),
    PURPLE_ENDERMAN(
            "13d9ec9c-2de1-4f0c-b579-2eeaf95b6ca7",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY5MDIwYzA3ZDg3NWJhZDE0NDAzMzdhZGI1NWEwOGMxNWRiMDZiOTk0NjQ2YTY5MTc5NWY0Y2QyOTNmZTNkZSJ9fX0="
    ),
    NEON_ENDERMAN(
            "88e9a827-7429-4c7f-9f49-dfb8aa2123d0",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTJkZWZiZTNjZGUzMjZkNDUxMWJiNTMzMzlkNzc3YWZhNzAzZjNlYzRkYWE2OTdkNjFhNDQwMjc0NGNiYjBjZCJ9fX0="
    ),
    ITEM_CORE_BLUE(
            "009a87cf-a01b-4f26-ba2b-75dbee0cea98",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThmNWRjOTI3OTIzMjc5MTI3YTlkMmFkZTg2NDMyZjk4Nzc2MDljYjlmODM4NTRhNWI4OTJiZjdjYWQ5ZGYyZiJ9fX0="
    ),
    //https://minecraft-heads.com/custom-heads/monsters/40957-white-spider
    WHITE_SPIDER(
            "5ca9d769-742d-4483-a8c5-1e42502bb779",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQ1Y2ViMjBiMGExYjNmNDU4ZWE0NTA0Y2QwYzI3MTJkYjJmZTk3OTRmY2Q3YTVlMTgwMTI3YTQ4ZWMyNjQ3MyJ9fX0="
    ),
    //https://minecraft-heads.com/custom-heads/monsters/44568-jungle-spider
    JUNGLE_SPIDER(
            "d822d63c-c59a-42e5-8f60-0b4b4bc323fd",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I1Y2NjZjRhYjExNDFjMzZmZmZiZmViZDZkMDlmMjVmMTBjODUxMmI1Y2JmMGMxNzRlMGQ1MzhjNmEzMThmMiJ9fX0="
    ),
    //https://minecraft-heads.com/custom-heads/monsters/52049-deep-dark-crawler
    DEEP_DARK_CRAWLER(
            "da308d95-f9c7-4406-9e49-48f6c5bb4125",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODk4ZTJmODU0YzJkMmRlNzU5NWIyMWJjMmY5MzYyMDA2ZWE3MmNiNjExODFkMmE5OWUwNjExY2Q3NDkwNzY5MyJ9fX0="
    ),
    //https://minecraft-heads.com/custom-heads/decoration/49995-spider-skull
    SPIDER(
            "80a7a6fd-ea9d-421f-9909-c2aa99789a93",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg1YzFlZTYxZjJiZDQ0M2MwYTllNjE3ZjM3MjAzY2RmZjQ0MGJmYTJkMDBiNmRkMzZmZjgzNGNkODcwMmQ5In19fQ="
    ),
    //https://minecraft-heads.com/custom-heads/monsters/23540-blood-spider
    BLOOD_SPIDER(
            "bf0be255-89ce-415a-b7ca-037424dce343",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODMwMDk4NmVkMGEwNGVhNzk5MDRmNmFlNTNmNDllZDNhMGZmNWIxZGY2MmJiYTYyMmVjYmQzNzc3ZjE1NmRmOCJ9fX0="
    ),
    //https://minecraft-heads.com/custom-heads/monsters/2533-decapitated-spider
    DECAPITATED_SPIDER(
            "0dd94060-fd14-486c-9339-2a896dbd991d",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ3YmVlNDJmZGE1ZmUyYjhhZTI2ZmNmNDE0MGNhNTRkNWQzMzRiMmFlMWZlZDlmMWMwOGJhZWI2M2E3ZiJ9fX0="
    ),
    //https://minecraft-heads.com/custom-heads/humanoid/48315-spider-spirit
    SPIDER_SPIRIT(
            "bded57c8-92a5-409b-9b6e-45788ce5c291",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzU3MWFmMGM5MjEyMzM3OTdkODU1YzUyMDBhOTBlOTVhMWQ3YzdhNDAxY2FhMThiYjdmZWEwMjFmY2E0OTE1MyJ9fX0="
    ),
    //https://minecraft-heads.com/custom-heads/decoration/45040-spider-egg-sac
    EGG_SAC(
            "7424d4ae-1103-468f-a4b9-2da89c2d7816",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTI4NmE2Mjg4NjRlZmVjNzZkMjFmMWJmYjg0ZDE4MDliMzAyZGVhYjcyOGI4ZGFiNmJlODA0NjdiN2U2ZmNlOCJ9fX0"
    ),
    //https://minecraft-heads.com/custom-heads/monsters/315-cave-spider
    CAVE_SPIDER(
            "39173a7a-c957-4ec1-ac1a-43e5a64983df",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2NDVkZmQ3N2QwOTkyMzEwN2IzNDk2ZTk0ZWViNWMzMDMyOWY5N2VmYzk2ZWQ3NmUyMjZlOTgyMjQifX19"
    );

    private final String id;
    private final String textureId;

    /**
     *
     * @param id uuid of the given custom skull.
     * @param textureId texture ID encoded in Base64, cannot be null.
     */
    SkullID(String id, @Nonnull String textureId) {
        this.id = id;
        this.textureId = textureId;
    }

    public String getTextureId() {
        return textureId;
    }

    public String getId() {
        return id;
    }
}
