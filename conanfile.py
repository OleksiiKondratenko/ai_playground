from conan import ConanFile


class AiPlaygroundConan(ConanFile):
    settings = "os", "compiler", "build_type", "arch"
    requires = "nlohmann_json/3.11.3"
    generators = "CMakeDeps"
