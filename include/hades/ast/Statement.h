//
// Created by dhruv on 07/08/20.
//

#ifndef HADES_STATEMENT_H
#define HADES_STATEMENT_H

#include "hades/ast/Expression.h"
#include "hades/ast/Type.h"
#include "hades/core/location.h"

namespace hades {

class Statement {
public:
  enum class Kind;

private:
  SourceLocation m_location;
  Kind m_kind;

protected:
  Statement(SourceLocation location, Kind kind) noexcept;

  auto location() const -> const SourceLocation &;
  auto kind() const -> Kind;

public:
  enum class Kind {
    ERROR,

    EXPRESSION,
    VAL,
  };
};

class ValStatement : public Statement {
  const Identifier m_name;
  const Optional<const Type *> m_annotation;
  const Expression *m_initializer;

public:
  ValStatement(SourceLocation, Identifier name,
               Optional<const Type *> annotation,
               const Expression *initializer) noexcept;
};

class ExpressionStatement : public Statement {
  const Expression *m_expression;
};

} // namespace hades

#endif // HADES_STATEMENT_H
