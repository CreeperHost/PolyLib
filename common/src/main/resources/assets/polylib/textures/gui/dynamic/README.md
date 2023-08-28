# Important note for resource packs developers
The textures in this directory are dynamically resized by the gui system.
This is done by slicing the textures in a 3x3 pattern, then the sections are tiled and stitched back together.
If you wish to customise these, they must be made in such a way that they won't look broken when cut up and tiled.
The standard border width is defined as 5 pixes from all sides, anything inside this will be cut up and tiled as part of the texture interior. 

# Important note for mod developers
Ideally we want to try to make live easy for the resource pack developers, so try to avoid using these as much as possible.
Instead, use the dynamic texture data generator to generate proper textures based on these dynamic textures.
This is better for performance, and it is better for the resource pack devs.

If your reading this and I have not implemented the dynamic texture datagen... well... you know who to yell at...    