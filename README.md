# ResourcesTracker

<p align="center">
  <a href="https://github.com/Nouridin/Resources-Tracker/releases"><img src="https://img.shields.io/github/v/release/Nouridin/Resources-Tracker?style=for-the-badge" alt="Version"></a>
  <a href="https://modrinth.com/plugin/resources-tracker"><img src="https://img.shields.io/modrinth/dt/resources-tracker?color=00AF5C&label=Downloads&style=for-the-badge" alt="Modrinth"></a>
  <a href="https://discord.gg/s2pevNCbv4"><img src="https://img.shields.io/discord/1403066205228503150?color=5865F2&label=Discord&logo=discord&logoColor=white&style=for-the-badge" alt="Discord"></a>
</p>

**The ultimate tool for server admins who want to maintain a fair and transparent gameplay environment.**

This lightweight, high-performance plugin silently logs player activity—specifically, which blocks are broken and which items are picked up. It provides you with the detailed insights you need to identify potential cheaters (like X-rayers) and keep a close eye on your server's economy and player progress.

## ✨ Core Features

*   **Comprehensive Logging:** Tracks every block broken and every item picked up by any player on the server.
*   **High-Performance Database:** Powered by **SQLite**, ResourcesTracker is designed for speed and scalability.
*   **Detailed Statistics Book:** Generate an in-game book with a complete, up-to-the-second statistical report for any player.
*   **Powerful Search:** Instantly filter a player's statistics for specific items directly from the command.
*   **Configurable Admin Alerts:** Receive real-time, in-game notifications when a player obtains a high-value item.
*   **Automatic Update Checker:** Stay informed! The plugin automatically checks for new versions on Modrinth.

## 🚀 Building from Source

To build ResourcesTracker yourself, you'll need:

*   Java Development Kit (JDK) 21 or higher
*   Apache Maven

Simply clone the repository and run the following command from the project's root directory:

```bash
mvn clean package
```

This will generate the plugin JAR file in the `target` directory.

## 🎮 How to Use

Using ResourcesTracker is simple. The main command is `/rstats`.

### View Player Statistics

To get a full statistical report for a player, use:

```
/rstats <player_name>
```

### Search for Specific Items

To narrow down the results to a specific item, simply add a search term:

```
/rstats <player_name> <search_term>
```

### Control Notifications

To toggle notifications for yourself:

```
/rstats notifications <enable|disable>
```

## ⚙️ Permissions

*   `resourcestracker.notify`: Players with this permission will receive real-time alerts. (Admins/Mods only)

*Note: By default, the `/rstats` command is OP-only, but this can be changed in the `config.yml`.*

## 🔧 Configuration

The `config.yml` file is simple and well-documented:

```yaml
# A list of materials that will trigger a notification.
notify-on:
  - DIAMOND_ORE
  - ANCIENT_DEBRIS

# Whether only OPs can use the /rstats command.
Only-OP: true

# Whether to check for new updates on startup.
check-for-updates: true
```

## 🤝 Contributing

Contributions are welcome! If you have a feature request, bug report, or pull request, please feel free to open an issue or submit a PR.

## 🔗 Links

*   **[Modrinth](https://modrinth.com/plugin/resources-tracker)** - Download the latest version of the plugin.
*   **[Discord](https://discord.gg/s2pevNCbv4)** - Join our community for support and discussion.
*   **[GitHub](https://github.com/Nouridin/Resources-Tracker)** - View the source code and contribute.

## 📄 License

This project is licensed under the [MIT License](LICENSE). *(You can add a LICENSE file with your chosen license)*
