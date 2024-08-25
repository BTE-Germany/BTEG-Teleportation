package de.btegermany.teleportation.TeleportationBukkit.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class Skulls {

    public static ItemStack getSkull(Skin skin) {
        return Skulls.getSkullFromId(skin.id);
    }

    public static ItemStack getSkullFromId(String id) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);

        try {
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.fromString("4da5d3b2-565d-11ef-b11e-325096b39f47")); // random uuid
            PlayerTextures playerTextures = playerProfile.getTextures();
            playerTextures.setSkin(new URL("http://textures.minecraft.net/texture/" + id));
            playerProfile.setTextures(playerTextures);

            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwnerProfile(playerProfile);
            head.setItemMeta(meta);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return head;
    }

    public enum Skin {

        ARROW_RIGHT ("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"),
        ARROW_LEFT ("cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5"),
        BL_BW ("c9bcb3a5d71d81be0d20ea715860a1a818c9c25f474de38d6e7e9c6f26f2b973"),
        BL_BY ("bf79448a48f8fbac041a1b7d43c95de4e8b27aff7ed58f31309afd299e17f4b"),
        BL_BE ("e69deaf62fe2abc0566cc056181f1b39bd4077135891dc5c68da4759ebee0488"),
        BL_BB ("e5120de6afa301eea0dfb54753c16f18fa946fb4fe75b949895263038f42974c"),
        BL_HB ("f1927bf69a040d54a89fca48f0c06d5a6bf9a19496c38bbf9c05a694938f7981"),
        BL_HH ("cc5fcfb1702ab8249c5d67eedb9da0def8e456d56ac9c0c426e2ca2c1c9172fe"),
        BL_HE ("e4167d50ec7b2eda09945f497e2eb65942c886a0f96fe09f2bf736f82d2d22a4"),
        BL_MV ("7865a2958561f814bfef0b98f785f7d34d07a7ef204144acde5e4d710375f11c"),
        BL_NI ("c372bdf5bf6058e87477ae1de65d048bd0481dc4611845ea35902fd7efb690ae"),
        BL_NW ("73f1566a1dc2fba5d1e13faacff65fea4e6770e1f03bec7d2faef327c69f7c00"),
        BL_RP ("7534040df875c87dc19f8db36b7dcb9052bd147f8ca5baa4ab6cee44eb7adb6d"),
        BL_SL ("6021dbd0144d908e2d0f87421cbafcfc57235604eacbf1a187beb47b3a98914d"),
        BL_SN ("c5f32dd2c0dd702f2937007b8ecd2e4309044350f66518f1d487f515e8bd6e54"),
        BL_SA ("f8432ff7f40ed8dcd8d9c054d2181fdec594134e1bc60e3cb0fb9505cfd16617"),
        BL_SH ("1c7472f3f38cb6542d16d324e65e6a16500b2021085932a2b32ec37732e1c2fd"),
        BL_TH ("a88d46648b76d54eb1f3a0a22d41cc7ad3672939a4ce01308dab74098751bab3"),
        ONE ("ca516fbae16058f251aef9a68d3078549f48f6d5b683f19cf5a1745217d72cc"),
        TWO ("4698add39cf9e4ea92d42fadefdec3be8a7dafa11fb359de752e9f54aecedc9a"),
        THREE ("fd9e4cd5e1b9f3c8d6ca5a1bf45d86edd1d51e535dbf855fe9d2f5d4cffcd2"),
        FOUR ("f2a3d53898141c58d5acbcfc87469a87d48c5c1fc82fb4e72f7015a3648058"),
        FIVE ("d1fe36c4104247c87ebfd358ae6ca7809b61affd6245fa984069275d1cba763"),
        SIX ("3ab4da2358b7b0e8980d03bdb64399efb4418763aaf89afb0434535637f0a1"),
        CITY_HOUSE ("b25b27ce62ca88743840a95d1c39868f43ca60696a84f564fbd7dda259be00fe"),
        WARP_HOUSE ("7b56e49085f55d5de215afd26fc4f1afe9c34313eff98e3e58245def06e5858c"),
        PLUS ("5ff31431d64587ff6ef98c0675810681f8c13bf96f51d9cb07ed7852b2ffd1"),
        MINUS ("4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46"),
        EDIT ("a7ed66f5a70209d821167d156fdbc0ca3bf11ad54ed5d86e75c265f7e5029ec1"),
        DICE ("8a084d0a1c6fc2163de30d8b148ab4d363220d5c972d5f88eb8dc86176ccdb3e"),
        HOME ("12d7a751eb071e08dbbc95bc5d9d66e5f51dc6712640ad2dfa03defbb68a7f3a");

        final String id;

        Skin(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

    }

}
