package apros.codeart.ddd.repository;

import apros.codeart.ddd.DomainDrivenException;
import apros.codeart.i18n.Language;

public class NotBeginTransactionException extends DomainDrivenException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7397013018616290378L;

	public NotBeginTransactionException() {
		super(Language.strings("NotOpenTransaction"));
	}
}