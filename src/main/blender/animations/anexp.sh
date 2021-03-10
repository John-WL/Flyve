#!/bin/sh

# it's way quicker to put stuff in background and then wait 2 seconds, than waiting for the whole queue to end its execution
for blendFileNames in */*.blend; do
  "C:\Program Files\Blender Foundation\Blender 2.83\blender" --background "$blendFileNames" --python .\\object_animation_export.py &
done

sleep 2