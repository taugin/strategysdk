import sys
import lief
import os

'''
修改so函数名称
'''
MODIFY_SO_FILE = True
def modify_function_name(file_path, from_str, to_str):
    so_dir = os.path.dirname(file_path)
    so_name = os.path.basename(file_path)
    name, ext = os.path.splitext(so_name)
    new_so_name = "{}_new{}".format(name, ext)
    print("\nModify so function name : {}".format(file_path))
    print("so_dir : {}, so_name : {}, name : {}, ext : {}, new_so_name : {}".format(so_dir, so_name, name, ext, new_so_name))
    new_file_path = os.path.join(so_dir, new_so_name)
    new_file_path = new_file_path.replace("library", "app")
    print("new file path : {}".format(new_file_path))
    new_file_dir = os.path.dirname(new_file_path)
    if (not os.path.exists(new_file_dir)):
        os.makedirs(new_file_dir)
    print("+++++++++++++++++++++++++++++++++")
    binary: lief.ELF.Binary = lief.ELF.parse(file_path)
    for s in binary.symbols:
        if from_str in s.name:
            original_name = s.name
            s.name = s.name.replace(from_str, to_str)
            print(original_name + " -> " + s.name)
    print("---------------------------------")
    if (MODIFY_SO_FILE):
        binary.write(new_file_path)
    return new_file_path

def show_modify_result(new_file_path, to_string):
    if MODIFY_SO_FILE:
        print("\nOutput the modify function name : {}".format(new_file_path))
        print("+++++++++++++++++++++++++++++++++")
        binary  = lief.parse(new_file_path)
        for item in binary.symbols:
            if to_string in item.name:
                print(item.name)
    print("---------------------------------")

if __name__ == '__main__':
    from_string = "Java_com_lazarus_Native"
    to_string = "Java_com_prettyus_Sweep"
    cur_dir = os.path.dirname(sys.argv[0])
    so_64 = os.path.join(cur_dir, "..", "library", "src", "main", "jniLibs", "arm64-v8a", "librarians.so")
    so_64 = os.path.normpath(so_64)
    so_32 = os.path.join(cur_dir, "..", "library", "src", "main", "jniLibs", "armeabi-v7a", "librarians.so")
    so_32 = os.path.normpath(so_32)

    new_file_path_64 = modify_function_name(so_64, from_string, to_string)
    show_modify_result(new_file_path_64, to_string)

    new_file_path_32 = modify_function_name(so_32, from_string, to_string)
    show_modify_result(new_file_path_32, to_string)

    print("package path : {}".format(to_string.replace("Java_", "").replace("_", ".")))