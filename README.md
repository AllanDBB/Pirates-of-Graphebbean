## Project Structure

The project is organized as a Maven Java application with the following key components:

### Core Components

- **Game Logic**: Located in [`org.abno.logic.components`](c:\Users\allan\Downloads\Pirates-of-Graphebbean\maincore\src\main\java\org\abno\logic\components)
- **User Interface**: Located in [`org.abno.frames`](c:\Users\allan\Downloads\Pirates-of-Graphebbean\maincore\src\main\java\org\abno\frames)
- **Network Communication**: Located in [`org.abno.sockets`](c:\Users\allan\Downloads\Pirates-of-Graphebbean\maincore\src\main\java\org\abno\sockets)

### Main Classes

1. **Player**: Represents a player with resources (money, iron), components, and weapons
2. **Components**: Various game elements like:
   - `EnergySource` - Provides energy (2x2 grid)
   - `Connector` - Connects components
   - `Market` - Trading place
   - `Mine` - Resource production
   - `WitchTemple` - Special abilities
   - `Armory` - Produces weapons

3. **Weapons**:
   - `Canon` - Basic weapon (500 iron)
   - `SuperCanon` - Advanced weapon (1000 iron)
   - `UltraCanon` - Powerful weapon (5000 iron)
   - `Bomb` - Explosive weapon (2000 iron)

4. **Networking**:
   - `Client`
   - `Server`

## Game Mechanics

The game appears to be a multiplayer strategy game where players:

1. **Build Components**: Players place various components on a 20x20 grid
2. **Connect Components**: Components form a graph structure using the `Graph` class
3. **Manage Resources**: Players have money and iron as resources
4. **Produce Weapons**: Armories produce different types of weapons
5. **Attack Other Players**: Players can use weapons to attack components on enemy boards
6. **Use Special Abilities**: Components like WitchTemple have special abilities (e.g., Kraken attack)
7. **Trade Resources**: Players can trade resources and items through the Market

## How to Run

1. Build the project using Maven:
   ```bash
   mvn clean package
   ```

2. Run the server:
   ```bash
   java -cp target/maincore-1.0-SNAPSHOT.jar org.abno.sockets.Server
   ```

3. Run the client(s):
   ```bash
   java -cp target/maincore-1.0-SNAPSHOT.jar org.abno.sockets.Client
   ```

## Commands

The game uses command-based interactions through the chat interface:

- `@Ready` - Mark yourself as ready to start
- `@UseCannon` - Use a cannon to attack
- `@UseSuperCannon` - Use a super cannon to attack
- `@UseUltraCannon` - Use an ultra cannon to attack multiple targets
- `@UseBomb` - Use a bomb to attack three locations
- `@ArmoryGenerate` - Generate a weapon from selected armory
- `@MineProduce` - Produce resources from a mine
- `@WitchTempleRandom` - Activate witch temple abilities
- `@BuyShip` - Purchase a ship to gather information
- `@SellComponentToMarket` - Sell a component to the market
- `@MyMoney` - Check your current money
- `@MyIron` - Check your current iron
- `@MyWeapons` - List your weapons

## Dependencies

The project uses the GraphStream library (version 2.0.0) for graph visualization and manipulation.

## License

This project is licensed under the MIT License. See the [LICENSE](c:\Users\allan\Downloads\Pirates-of-Graphebbean\LICENSE) file for details.