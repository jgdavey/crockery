SHELL := bash
.ONESHELL:
.SUFFIXES:
.SHELLFLAGS := -eu -o pipefail -c
MAKEFLAGS += --no-builtin-rules

ifeq ($(VERSION),)
	VERSION:=$(shell cat VERSION)
endif

CLJ_FILES=$(shell find src -name '*.clj' -o -name '*.cljc')
CLJS_FILES=$(shell find src -name '*.cljs' -o -name '*.cljc')
EDN_FILES=$(wildcard *.edn)

JAR_DEPS=$(EDN_FILES) $(CLJ_FILES)

TARGET_DIR:=target
OUTPUT_JAR=$(TARGET_DIR)/com.joshuadavey/crockery-$(VERSION).jar

.PHONY: clean deploy test test-clj test-cljs test-bb

.DEFAULT_GOAL:=jar

test-clj:
	clojure -M:test:test-clj

test-cljs:
	clojure -M:test:test-cljs

test-bb:
	bb test:bb

test: test-cljs test-clj test-bb

jar: $(OUTPUT_JAR)

$(OUTPUT_JAR): $(JAR_DEPS)
	clojure -T:build build-jar

deploy: $(OUTPUT_JAR)
	clojure -T:build deploy

clean:
	@rm -rf $(TARGET_DIR) cljs-test-runner-out
