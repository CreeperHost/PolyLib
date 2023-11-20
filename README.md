PolyLib

### Important note for resource packs developers
The textures in the textures/gui/dynamic directory are dynamically resized by the gui system.
This is done by slicing the textures in a 3x3 pattern, then the sections are tiled and stitched back together.
If you wish to customise these, they must be made in such a way that they won't look broken when cut up and tiled.
The standard border width is defined as 5 pixes from all sides, anything inside this will be cut up and tiled as part of the texture interior.

### Related note for mod developers
Ideally we want to try to make life easy for the resource pack developers, so try to avoid using dynamic textures as much as possible.
They are fine for things like buttons that come in all shapes and sizes, but for things like gui backgrounds,
use the dynamic texture data generator to generate proper textures based on the dynamic textures.
This is better for performance, and it is better for the resource pack devs.