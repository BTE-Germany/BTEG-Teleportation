# Velocity
- 2.0.0-SNAPSHOT:
  - proxy now Velocity + changed plugin messaging channel

# BungeeCord
- 1.1:
  - Support for Minecraft 1.20.1
  - Java 17
  - Caching all warps
  - Enum for federal states
- 1.1.1:
  - Improved CoordinatesConverter (conversion degrees + minutes (+ seconds) -> degrees)
  - sending only parts (next page) of gui content in order to reduce sent package sizes
- 1.1.2:
  - fixed problems with /nwarp create and /nwarp change
- 1.1.3:
  - activated StatesBordersCheck (if state on other server send to this server)
- 1.1.4:
  - added warning for inaccurate coordinate formats
  - added /event
  - removed normen hubs from nwarp gui
  - added warp tags
- 1.1.5
  - added command for editing tags
  - reusing warp ids
- 1.1.6
  - new system for plugin messages
  - sending warning everytime an inaccurate coordinate format is used for /tpll
  - disabled Terramap support