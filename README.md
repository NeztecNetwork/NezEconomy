# NezEconomy

The best economy plugin for PowerNukkitX 2.0

![License](https://img.shields.io/github/license/NeztecNetwork/NezEconomy)
![Release](https://img.shields.io/github/v/release/NeztecNetwork/NezEconomy)

## Features

- **Multiple storage backends** – YAML, SQLite, MySQL (switch via config)
- **Thread‑safe** – per‑account locks, no data corruption
- **Simple API** – other plugins can integrate with one line
- **Custom events** – listen to `MoneyChangeEvent`
- **Top balances** – `/baltop` with pagination
- **Transaction logging** – every change is recorded
- **Reloadable config** – no restart needed
- **Configurable starting balance**

## Installation

1. Download the latest `.jar` from the [Releases](https://github.com/NeztecNetwork/NezEconomy/releases) page.
2. Place it in your server’s `plugins/` folder.
3. Start (or restart) the server.
4. Configure `plugins/NezEconomy/config.yml` to your liking.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/money` | Check your balance | none |
| `/pay <player> <amount>` | Send money to another player | none |
| `/baltop [page]` | View richest players | none |
| `/nezeconomy <set/add/remove> <player> <amount>` | Admin economy management | `nezeconomy.admin` |
| `/nezeco reload` | Reload config | `nezeconomy.admin` |

## API Usage (for developers)

Add `NezEconomy` as a soft dependency in your `plugin.yml`:

```yaml
softdepend: [NezEconomy]
```

Then hook into the API:

```java
import com.nez.economy.api.EconomyAPI;
import com.nez.economy.api.EconomyProvider;

EconomyProvider eco = EconomyAPI.getInstance();
eco.addMoney(player, 100.0);
```

## Building from Source

Requirements: JDK 21+, Maven

```bash
git clone https://github.com/NeztecNetwork/NezEconomy.git
cd NezEconomy
mvn clean package
```

The compiled `.jar` will be in `target/NezEconomy.jar`.

## Configuration

Edit `config.yml`:

```yaml
currency:
  symbol: "$"
  format: "#,###.##"
storage:
  type: sqlite   # yaml, sqlite, mysql
starting-balance: 100.0
```

## License

This project is licensed under the MIT License.
