package com.sppad.jots.construction;

public enum SetStrategy
{
  /** Any field can be set */
  ALL,

  /** Fields that are annotated can be set */
  ANNOTATED_ONLY,

  /** Fields with a setter or that are annotated can be set */
  SETTERS_OR_ANNOTATED,

  /** Fields with a setter and that are annotated can be set */
  SETTERS_AND_ANNOTATED
}
