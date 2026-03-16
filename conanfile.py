from conan import ConanFile


class AiPlaygroundConan(ConanFile):
    settings = "os", "compiler", "build_type", "arch"
    requires = "cairo/1.18.4"
    generators = "CMakeDeps"
