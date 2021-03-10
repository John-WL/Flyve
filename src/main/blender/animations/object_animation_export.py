import bpy

def main():
    #destination folder
    export_path = "C:\\Users\\John\\Documents\\GitHub\\RocketLeague-PanBot\\src\\main\\resources\\boss rig\\"
    #file extension when exported
    export_extension = ".cop"

    every_scene = bpy.context.window.scene
    
    file_name = bpy.path.basename(bpy.context.blend_data.filepath)
    file_name_without_extension = file_name[0:len(file_name)-6]
    
    #open a file at the export location
    file = open(export_path + file_name_without_extension + export_extension, 'w')
    #frames
    for every_frame in range(every_scene.frame_start, every_scene.frame_end+1):
        #frame set
        every_scene.frame_set(every_frame)
        #objects
        for every_object in every_scene.objects:
            #get the location of the car
            location = every_object.location

            #get the orientation of the car in euler XYZ
            #the quaternions are used to prevent any accidental rotation
            every_object.rotation_mode = 'QUATERNION'
            every_object.rotation_mode = 'XYZ'
            orientation = every_object.rotation_euler

            #scene scale vs rocket league scale: 1:10
            loc_scale = 10

            #send the data!
            file.write(
                #car id in blender
                str(every_object.name)[16:19] + ':'
                #frame id
                + str(every_scene.frame_current) + ':'

                #location in x and y are reversed
                + str(-location.x * loc_scale) + ':'
                + str(-location.y * loc_scale) + ':'
                + str( location.z * loc_scale) + ':'

                #euler angles are funky in rocket league!
                + str( orientation.x) + ':'
                + str(-orientation.y) + ':'
                + str( orientation.z) + '\n'
            )
    file.close()

if __name__ == "__main__":
    main()