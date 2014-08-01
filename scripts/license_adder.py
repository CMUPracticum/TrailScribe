# Copyright (c) 2014, TrailScribe Team.
# This content is released under the MIT License. See the file named LICENSE for details.
import os
import sys

def add_license(license_filename_prefix, src_dir_path):

    # Use os.walk() to traverse src_dir_path recursively. It will return a list
    # of tuples, each tuple represents "directory path", "subdirectories", and 
    # "files".
    #
    # For example, ('test/edu/cmu/sv/trailscribe/controller', [], 
    # ['MainController.java', 'MapsController.java', 'SynchronizationCenterController.java'])
    for path, subdirs, files in os.walk(src_dir_path):
        if len(files) == 0:
            continue

        for src_filename in files:
            license_filename = None

            if src_filename.endswith('.java'):
                license_filename = license_filename_prefix + '_java.txt'
            elif src_filename.endswith('.py'):
                license_filename = license_filename_prefix + '_python.txt'
            else:
                continue

            src_file_path = os.path.join(path, src_filename)
            src_file = open(src_file_path, 'r')
            src_first_line = src_file.readline()
            src_second_line = src_file.readline()
            src_file.close()

            license_file = open(license_filename, 'r')
            license_first_line = license_file.readline()
            license_file.close()

            # Need to compare the first two lines because the first line in 
            # the license file for Java is "/*"
            if license_first_line in src_first_line or license_first_line in src_second_line:
                continue

            # Crate backup for source code
            src_file_backup_path = src_file_path + '.backup'
            mv_command = 'mv {0} {1}'.format(src_file_path, src_file_backup_path)
            os.system(mv_command)

            # Merge the content of the license file and source code
            cat_command = 'cat {0} {1} > {2}'.format(license_filename, src_file_backup_path, src_file_path)
            os.system(cat_command)

	    # Remove the backup file
	    rm_command = 'rm -f {0}'.format(src_file_backup_path)
	    os.system(rm_command)


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print "Wrong number of arguments. Usage: python license_adder.py <license_file_prefix> <src_folder_path>"
        sys.exit()

    license_filename_prefix = sys.argv[1]
    src_dir_path = sys.argv[2]

    license_filename_java = license_filename_prefix + '_java.txt'
    license_filename_python = license_filename_prefix + '_python.txt'
    
    if not (os.path.isfile(license_filename_java) and os.path.isfile(license_filename_python)):
        print "License file for Java or Python are not found. Their names should be {0} and {1}. License files and this script should be in the same folder.".format(license_filename_java, license_filename_python)
        sys.exit()

    if not os.path.isdir(src_dir_path):
        print "Source code folder is not found."
        sys.exit()

    add_license(license_filename_prefix, src_dir_path)
