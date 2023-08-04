import sys
import lief
import os

#https://github.com/lief-project/LIEF
'''
修改so函数名称
'''
MODIFY_SO_FILE = True
def modify_function_name(file_path, new_file_path, from_str, to_str):
    new_file_dir = os.path.dirname(new_file_path)
    if (not os.path.exists(new_file_dir)):
        os.makedirs(new_file_dir)
    print("+++++++++++++++++++++++++++++++++")
    binary: lief.ELF.Binary = lief.ELF.parse(file_path)
    for s in binary.symbols:
        if from_str in s.name:
            original_name = s.name
            if to_str != None and len(to_str) > 0:
                s.name = s.name.replace(from_str, to_str)
                print(original_name + " -> " + s.name)
            else:
                print(s.name)
    print("---------------------------------")
    if to_str != None and len(to_str) > 0 and MODIFY_SO_FILE:
        binary.write(new_file_path)
    return new_file_path

def show_modify_result(new_file_path, to_string):
    if to_string != None and len(to_string) > 0 and MODIFY_SO_FILE:
        print("\nOutput the modify function name : {}".format(new_file_path))
        print("+++++++++++++++++++++++++++++++++")
        binary  = lief.parse(new_file_path)
        for item in binary.symbols:
            if to_string in item.name:
                print(item.name)
        print("package path : {}".format(to_string.replace("Java_", "").replace("_", ".")))
    print("---------------------------------")

def generate_new_file(src_file):
    so_dir = os.path.dirname(src_file)
    so_name = os.path.basename(src_file)
    name, ext = os.path.splitext(so_name)
    new_so_name = "{}_new{}".format(name, ext)
    new_file_path = os.path.join(so_dir, new_so_name)
    print("new_file_path : {}".format(new_file_path))
    return new_file_path

def modify_lazarus_so():
    from_string = "Java_com_lazarus_Native"
    to_string = "Java_com_prettyus_Sweep"
    cur_dir = os.path.dirname(sys.argv[0])
    so_64 = os.path.join(cur_dir, "..", "library", "src", "main", "jniLibs", "arm64-v8a", "librarians.so")
    so_64 = os.path.normpath(so_64)
    so_64_new = generate_new_file(so_64)
    so_32 = os.path.join(cur_dir, "..", "library", "src", "main", "jniLibs", "armeabi-v7a", "librarians.so")
    so_32 = os.path.normpath(so_32)
    so_32_new = generate_new_file(so_32)

    new_file_path_64 = modify_function_name(so_64, so_64_new, from_string, to_string)
    show_modify_result(new_file_path_64, to_string)

    new_file_path_32 = modify_function_name(so_32, so_32_new, from_string, to_string)
    show_modify_result(new_file_path_32, to_string)

def modify_unzip_so():
    from_string = "Java_com_hzy_lib7z_Un7Zip_un7zip"
    to_string = "Java_com_up_tools_Un7Zip_un7zip"
    so_base_dir = r"F:\myprojects\appcode\UpCleaner\app\src\main\jniLibs"
    so_64 = os.path.join(so_base_dir, "arm64-v8a", "libun7zip.so")
    so_64 = os.path.normpath(so_64)
    so_64_new = generate_new_file(so_64)
    so_32 = os.path.join(so_base_dir, "armeabi-v7a", "libun7zip.so")
    so_32 = os.path.normpath(so_32)
    so_32_new = generate_new_file(so_32)

    new_file_path_64 = modify_function_name(so_64, so_64_new, from_string, to_string)
    show_modify_result(new_file_path_64, to_string)
    new_file_path_32 = modify_function_name(so_32, so_32_new, from_string, to_string)
    show_modify_result(new_file_path_32, to_string)

if __name__ == '__main__':
    modify_unzip_so()