package com.gamerforea.scdetector.similar;

import java.util.ArrayList;
import java.util.List;

public class ClassContainer
{
	public final String name;
	public final String superName;
	public final Type type;

	public List<String> interfaces = new ArrayList<>();
	public List<String> strings = new ArrayList<>();
	public List<FieldOrMethod> fields = new ArrayList<>();
	public List<FieldOrMethod> methods = new ArrayList<>();

	public ClassContainer(String name, String superName, Type type)
	{
		this.name = name;
		this.superName = superName;
		this.type = type;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	public static enum Type
	{
		ABSTRACT, CLASS, INTERFACE, ENUM;
	}
}