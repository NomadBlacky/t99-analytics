terraform {
  required_version = "= 0.11.13"

  backend "s3" {
    bucket = "com.github.nomadblacky.terraform"
    key    = "t99-analytics.tfstate"
    region = "ap-northeast-1"
  }
}

variable "datadog_api_key" {
  type = "string"
}

variable "datadog_app_key" {
  type = "string"
}

provider "datadog" {
  api_key = "${var.datadog_api_key}"
  app_key = "${var.datadog_app_key}"
}
