from conan import ConanFile


class AiPlaygroundConan(ConanFile):
    settings = "os", "compiler", "build_type", "arch"
    requires = "boost/1.86.0"
    generators = "CMakeDeps"
